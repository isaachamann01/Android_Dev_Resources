package com.atakmap.android.plugintemplate.plugin;

import android.graphics.Bitmap;
import android.util.Pair;

import com.atakmap.coremap.maps.coords.GeoBounds;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.map.MapRenderer;
import com.atakmap.map.layer.Layer;
import com.atakmap.map.layer.control.SurfaceRendererControl;
import com.atakmap.map.layer.feature.geometry.Envelope;
import com.atakmap.map.layer.opengl.GLAbstractLayer;
import com.atakmap.map.layer.opengl.GLLayer2;
import com.atakmap.map.layer.opengl.GLLayerSpi2;
import com.atakmap.map.opengl.GLMapView;
import com.atakmap.opengl.GLES20FixedPipeline;
import com.atakmap.opengl.GLTexture;
import com.atakmap.util.Visitor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public class GLCoverageMapLayer extends GLAbstractLayer {

    public final static GLLayerSpi2 SPI = new GLLayerSpi2() {
        @Override
        public int getPriority() {
            return 1;
        }

        @Override
        public GLLayer2 create(Pair<MapRenderer, Layer> object) {
            if (!(object.second instanceof CoverageMapLayer))
                return null;
            return new GLCoverageMapLayer(object.first,
                    (CoverageMapLayer) object.second);
        }
    };

    private Data frame;
    private final CoverageMapLayer subject;

    public GLCoverageMapLayer(MapRenderer surface,
                              CoverageMapLayer subject) {
        super(surface, subject);
        this.subject = subject;
    }

    @Override
    protected void init() {
        super.init();
        this.frame = new Data();
        setData(subject.layerARGB, subject.layerWidth, subject.layerHeight,
                subject.upperLeft, subject.upperRight, subject.lowerRight,
                subject.lowerLeft);
    }

    @Override
    protected void drawImpl(GLMapView view) {

        view.forward(frame.points, frame.vertexCoordinates);
        if (frame.texture == null) {
            return;
        }
        frame.texture.draw(4, GLES20FixedPipeline.GL_FLOAT,
                frame.textureCoordinates, frame.vertexCoordinates);

    }

    @Override
    public void release() {
        if (this.frame != null && this.frame.texture != null)
            this.frame.texture.release();
        this.frame = null;
        super.release();
    }

    public void setData(int[] argb,
                        int width, int height,
                        GeoPoint upperLeft,
                        GeoPoint upperRight, GeoPoint lowerRight, GeoPoint lowerLeft) {
        Bitmap bitmap = Bitmap.createBitmap(argb, width, height, Bitmap.Config.ARGB_8888);
        GeoPoint ul = new GeoPoint(upperLeft);
        GeoPoint ur = new GeoPoint(upperRight);
        GeoPoint lr = new GeoPoint(lowerRight);
        GeoPoint ll = new GeoPoint(lowerLeft);

        this.renderContext.queueEvent(new Runnable() {
            public void run() {
                try {
                    if (frame != null)
                        frame.update(bitmap, width, height, ul, ur,
                                lr, ll);
                } finally {
                    // cleanup the bitmap
                    bitmap.recycle();
                }
            }
        });

    }


    private static class Data {
        GLTexture texture;
        final DoubleBuffer points;
        final FloatBuffer vertexCoordinates;
        final ByteBuffer textureCoordinates;

        Data() {
            this.texture = null;
            this.points = ByteBuffer.allocateDirect(8 * 2 * 4)
                    .order(ByteOrder.nativeOrder()).asDoubleBuffer();
            this.vertexCoordinates = ByteBuffer.allocateDirect(4 * 2 * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.textureCoordinates = ByteBuffer.allocateDirect(4 * 2 * 4)
                    .order(ByteOrder.nativeOrder());
        }

        void update(Bitmap frame, final int width, final int height,
                    final GeoPoint ul, final GeoPoint ur, final GeoPoint lr,
                    final GeoPoint ll) {

            if (this.texture == null
                    || (this.texture.getTexWidth() < width || this.texture
                    .getTexHeight() < height)) {
                if (this.texture != null)
                    this.texture.release();
                this.texture = new GLTexture(width, height, frame.getConfig());
            }

            this.texture.load(null, 0, 0, width, height);

            this.textureCoordinates.clear();
            this.textureCoordinates.putFloat(0.0f); // upper-left
            this.textureCoordinates.putFloat(0.0f);
            this.textureCoordinates.putFloat((float) width
                    / (float) this.texture.getTexWidth()); // upper-right
            this.textureCoordinates.putFloat(0.0f);
            this.textureCoordinates.putFloat((float) width
                    / (float) this.texture.getTexWidth()); // lower-right
            this.textureCoordinates.putFloat((float) height
                    / (float) this.texture.getTexHeight());
            this.textureCoordinates.putFloat(0.0f); // lower-left
            this.textureCoordinates.putFloat((float) height
                    / (float) this.texture.getTexHeight());
            this.textureCoordinates.flip();

            this.points.clear();
            this.points.put(ul.getLongitude());
            this.points.put(ul.getLatitude());
            this.points.put(ur.getLongitude());
            this.points.put(ur.getLatitude());
            this.points.put(lr.getLongitude());
            this.points.put(lr.getLatitude());
            this.points.put(ll.getLongitude());
            this.points.put(ll.getLatitude());
            this.points.flip();

            this.texture.load(frame);
        }
    }
}

