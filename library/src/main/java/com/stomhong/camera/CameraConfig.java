package com.stomhong.camera;

/**
 * 配置摄像头参数类
 */
public final class CameraConfig {
    /**
     * 摄像头宽度
     */
    private int mWidth;
    /**
     * 摄像头高度
     */
    private int mHeight;
    /**
     * 帧率
     */
    private int mFps;

    private CameraConfig() {
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getFps() {
        return mFps;
    }

    public static class Builder {
        private int height;
        private int width;
        private int fps;

        public Builder() {
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setFps(int fps) {
            this.fps = fps;
            return this;
        }

        /**
         * must call build() on the end
         *
         * @return CameraConfig
         */
        public CameraConfig build() {
            CameraConfig config = new CameraConfig();
            config.mWidth = this.width;
            config.mHeight = this.height;
            config.mFps = this.fps;
            return config;
        }
    }

}
