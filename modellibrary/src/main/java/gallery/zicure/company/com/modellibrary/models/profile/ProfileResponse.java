package gallery.zicure.company.com.modellibrary.models.profile;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by macintosh on 17/1/18.
 */

public class ProfileResponse {
    @SerializedName("status") private String status;
    @SerializedName("result") private ProfileResult result;

    public String getStatus() {
        return status;
    }

    public ProfileResult getResult() {
        return result;
    }

    public static class ProfileResult {
        @SerializedName("Data") private ProfileData data;
        @SerializedName("message") private String message;


        public String getMessage() {
            return message;
        }

        public ProfileData getData() {
            return data;
        }

        public void setData(ProfileData data) {
            this.data = data;
        }

        public static class ProfileData {
            @SerializedName("Profiles") private ProfileDetail detail;
            @SerializedName("Languages") private ProfileLanguage language;
            @SerializedName("Menus") private ArrayList<ProfileMenu> menus;

            public ProfileData() {
            }

            public ProfileDetail getDetail() {
                return detail;
            }

            public ProfileLanguage getLanguage(){
                return language;
            }

            public ArrayList<ProfileMenu> getMenus() {
                return menus;
            }

            public class ProfileMenu {
                @SerializedName("label") private String label;
                @SerializedName("url") private String url;

                public String getLabel() {
                    return label;
                }

                public String getUrl() {
                    return url;
                }
            }

            public static class ProfileLanguage {
                @SerializedName("label") private String label;
                @SerializedName("list") private ArrayList<ListLanguage> arrLanguage;

                public String getLabel() {
                    return label;
                }

                public ArrayList<ListLanguage> getArrLanguage() {
                    return arrLanguage;
                }

                public void setArrLanguage(ArrayList<ListLanguage> arrLanguage) {
                    this.arrLanguage = arrLanguage;
                }


                public static class ListLanguage {
                    @SerializedName("code") private String code;
                    @SerializedName("value") private String value;


                    public String getCode() {
                        return code;
                    }

                    public void setCode(String code) {
                        this.code = code;
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }
                }
            }

            public class ProfileDetail {
                @SerializedName("firstname") private String firstName;
                @SerializedName("lastname") private String lastName;
                @SerializedName("gender") private String gender;
                @SerializedName("img_path") private String imgPath;

                public String getFirstName() {
                    return firstName;
                }

                public String getLastName() {
                    return lastName;
                }

                public String getGender() {
                    return gender;
                }

                public String getImgPath() {
                    return imgPath;
                }
            }
        }
    }
}
