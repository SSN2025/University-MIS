package All.Factory;

import All.Webpages.*;

public class PageFactory {
    public static Page getPage(String Type,String Id){
        if(Type.equalsIgnoreCase("Student")){
            return  new StudentPage(Id);
        }
        else if(Type.equalsIgnoreCase("Professor")){
            return  new ProfessorPage(Id);
        }
        else if(Type.equalsIgnoreCase("Administrator")){
            return  new AdministratorPage(Id);
        }
        else if(Type.equalsIgnoreCase("TeachingAssistant")){
            return new TeachingAssistantPage(Id);
        }
        else{
            return null;
        }
    }
}
