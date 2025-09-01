package com.example.fashionstore;

        import android.app.Application;
        import com.cloudinary.android.MediaManager;
        import java.util.HashMap;
        import java.util.Map;

        public class MyApp extends Application {
            @Override
            public void onCreate() {
                super.onCreate();

                // Cloudinary config
                Map<String, String> config = new HashMap<>();
                config.put("cloud_name", "dmnklscma");   // from dashboard
                config.put("api_key", "252985529765673");         // from dashboard
                config.put("api_secret", "CXpMlA61V_mwGXqAYHibiE8YTMo"); // use your actual secret

                try {
                    MediaManager.init(this, config);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }