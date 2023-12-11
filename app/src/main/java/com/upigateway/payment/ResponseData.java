package com.upigateway.payment;

public class ResponseData {

    private boolean status;
    private String msg;
    private Data data;


    public static class Data {
        private long order_id;
        private String payment_url;
        private String upi_id_hash;

        public Data(long order_id, String payment_url, String upi_id_hash) {
            this.order_id = order_id;
            this.payment_url = payment_url;
            this.upi_id_hash = upi_id_hash;
        }


        public long getOrder_id() {
            return order_id;
        }

        public void setOrder_id(long order_id) {
            this.order_id = order_id;
        }

        public String getPayment_url() {
            return payment_url;
        }

        public void setPayment_url(String payment_url) {
            this.payment_url = payment_url;
        }

        public String getUpi_id_hash() {
            return upi_id_hash;
        }

        public void setUpi_id_hash(String upi_id_hash) {
            this.upi_id_hash = upi_id_hash;
        }
    }

    public ResponseData(boolean status, String msg, Data data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
