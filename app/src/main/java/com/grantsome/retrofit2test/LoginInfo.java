package com.grantsome.retrofit2test;

/**
 * Created by Grantsome on 2017/8/7.
 */

public class LoginInfo {
    /**
     * status : 200
     * info : success
     * data : {"id":1,"username":"admin","avatar":null,"token":"6c5f989bdc56fe25f8a2b08443f354c910280c50"}
     */

    private int status;
    private String info;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "status=" + status +
                ", info='" + info + '\'' +
                ", data=" + data +
                '}';
    }

    public static class DataBean {
        /**
         * id : 1
         * username : admin
         * avatar : null
         * token : 6c5f989bdc56fe25f8a2b08443f354c910280c50
         */

        private int id;
        private String username;
        private Object avatar;
        private String token;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Object getAvatar() {
            return avatar;
        }

        public void setAvatar(Object avatar) {
            this.avatar = avatar;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", avatar=" + avatar +
                    ", token='" + token + '\'' +
                    '}';
        }
    }
}
