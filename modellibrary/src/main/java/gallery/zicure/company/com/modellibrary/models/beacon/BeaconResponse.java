package gallery.zicure.company.com.modellibrary.models.beacon;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pakgon on 9/19/2017 AD.
 */

public class BeaconResponse {
    @SerializedName("result") private BeaconResult result;

    public BeaconResult getResult() {
        return result;
    }

    public void setResult(BeaconResult result) {
        this.result = result;
    }

    public static class BeaconResult {
        @SerializedName("Success") private String success;
        @SerializedName("Error") private String error;
        @SerializedName("Data") private BeaconData beaconData;

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public BeaconData getBeaconData() {
            return beaconData;
        }

        public void setBeaconData(BeaconData beaconData) {
            this.beaconData = beaconData;
        }


        public static class BeaconData {
            @SerializedName("BlocUrl") private String blocUrl;

            public String getBlocUrl() {
                return blocUrl;
            }

            public void setBlocUrl(String blocUrl) {
                this.blocUrl = blocUrl;
            }
        }
    }
}
