package All.Webpages;

public class FeedBack <TYPE>{
    TYPE data;
    FeedBack(TYPE d){
        data = d;
    }
    String getFeedBack(){
        if(data instanceof String)
        return (String)data;
        else return ("Rating: " + data + " star");
    }
}
