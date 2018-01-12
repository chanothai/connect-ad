package gallery.zicure.company.com.modellibrary.models.beacon;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pakgon on 9/19/2017 AD.
 */

public class BeaconRequest {
    @SerializedName("BeaconDetail") private BeaconDetail beaconDetail;
    public BeaconDetail getBeaconDetail() {
        return beaconDetail;
    }

    public void setBeaconDetail(BeaconDetail beaconDetail) {
        this.beaconDetail = beaconDetail;
    }

    public class BeaconDetail {
        @SerializedName("token") private String token;
        @SerializedName("uuid") private String uuid;
        @SerializedName("timestamp") private String timeStamp;

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

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }
    }
}
