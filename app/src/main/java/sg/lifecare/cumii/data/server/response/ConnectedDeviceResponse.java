package sg.lifecare.cumii.data.server.response;

import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.InstanceCreator;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import sg.lifecare.cumii.data.CumiiUtil;
import timber.log.Timber;

public class ConnectedDeviceResponse extends Response {

    @SerializedName("Data")
    private List<Data> data;

    @Override
    public List<Data> getData() {
        return data;
    }

    public class Data {

        private String _id;
        private Product product;
        private Date last_update;
        private String type;
        private String value;
        private String status;
        private String name;
        private String password;

        public String getId() {
            return _id;
        }

        public String getName() {
            return name;
        }

        public String getPassword() {
            return password;
        }

        public String getStatus() {
            return status;
        }

        public int getProductType() {
            if (product != null) {
                return product.getProductType();

            }

            return CumiiUtil.UNKNOWN_PRODUCT_TYPE;
        }

        public boolean isOnline() {
            return "N".equalsIgnoreCase(status);
        }

        public int getValue() {
            if (TextUtils.isEmpty(value)) {
                return -1;
            }

            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                Timber.e(e.getMessage());
            }
            return -1;
        }

        public String getImageUrl() {
            if ((product != null) && (product.getMedias() != null) && (product.getMedias().size() > 0)) {
                return product.getMedias().get(0).getMediaUrl();
            }

            return "";
        }

        public Date getLastUpdate() {
            return last_update;
        }
    }

    class Product {
        public static final String PRODUCT_JSW_CAMERA = "58ddf62e2b3bec1c1cdfae2c";

        private String _id;
        private List<Media> medias;
        private boolean gsm;
        private boolean battery_level_compatibitity;
        private boolean uptime_compatibility;
        private String name;

        List<Media> getMedias() {
            return medias;
        }

        public int getProductType() {
            if (!TextUtils.isEmpty(_id)) {
                if (PRODUCT_JSW_CAMERA.equals(_id)) {
                    return CumiiUtil.JSW_CAMERA_PRODUCT_TYPE;
                }

            }

            return CumiiUtil.UNKNOWN_PRODUCT_TYPE;
        }
    }
}
