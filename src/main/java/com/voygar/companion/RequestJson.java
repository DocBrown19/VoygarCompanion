package com.voygar.companion;

public class RequestJson {
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String player; private double x,y,z; private String dim; private String prompt;
        public Builder player(String p){this.player=p;return this;}
        public Builder x(double v){this.x=v;return this;}
        public Builder y(double v){this.y=v;return this;}
        public Builder z(double v){this.z=v;return this;}
        public Builder dimension(String d){this.dim=d;return this;}
        public Builder prompt(String p){this.prompt=p;return this;}
        public String build(){
            return String.format("{\"player\":\"%s\",\"x\":%.2f,\"y\":%.2f,\"z\":%.2f,\"dimension\":\"%s\",\"prompt\":\"%s\"}",
                    player,x,y,z,dim,prompt.replace("\"", "'"));
        }
    }
}
