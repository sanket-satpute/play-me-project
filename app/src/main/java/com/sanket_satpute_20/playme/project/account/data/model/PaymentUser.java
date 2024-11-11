package com.sanket_satpute_20.playme.project.account.data.model;

import java.io.Serializable;
import java.util.ArrayList;

    public class PaymentUser implements Serializable {
        private ArrayList<String> userDocumentId;


        public PaymentUser() {
        }

        public PaymentUser(ArrayList<String> userDocumentId) {
            this.userDocumentId = userDocumentId;
        }

        public ArrayList<String> getUserDocumentId() {
            return userDocumentId;
        }

        public void setUserDocumentId(ArrayList<String> userDocumentId) {
            this.userDocumentId = userDocumentId;
        }
    }
