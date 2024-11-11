package com.sanket_satpute_20.playme.project.account.data.documentation;

public class AppDocsComments {

    private char dot_char = '\u2B24';//'\u25CF';
    private String main_topic;
    private String detail_of_topic;

    public AppDocsComments(char dot_char, String main_topic, String detail_of_topic) {
        this.dot_char = dot_char;
        this.main_topic = main_topic;
        this.detail_of_topic = detail_of_topic;
    }

    public AppDocsComments(String main_topic, String detail_of_topic) {
        this.main_topic = main_topic;
        this.detail_of_topic = detail_of_topic;
    }

    public char getDot_char() {
        return dot_char;
    }

    public String getMain_topic() {
        return main_topic;
    }

    public String getDetail_of_topic() {
        return detail_of_topic;
    }
}
