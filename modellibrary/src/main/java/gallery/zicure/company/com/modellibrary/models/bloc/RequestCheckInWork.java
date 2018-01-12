package gallery.zicure.company.com.modellibrary.models.bloc;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pakgon on 10/17/2017 AD.
 */

public class RequestCheckInWork {
    @SerializedName("Data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("token") private String token;
        @SerializedName("uuid") private String uuid;
        @SerializedName("Geo") private LocationDevice location;
        @SerializedName("device_id") private String deviceId;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public LocationDevice getLocation() {
            return location;
        }

        public void setLocation(LocationDevice location) {
            this.location = location;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public static class LocationDevice {
            @SerializedName("longitude") private String longitude;
            @SerializedName("latitude") private String latitude;

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }

            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }
        }
    }
}
