
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.force.api.http.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.web.*;

public class Main 
{
	public static void main( String[] args )
	{
		
		Http 		 http = new Http();
		HttpRequest  req  = new HttpRequest();
		HttpResponse res  = null;
		String endpoint   = "https://screepspl.us/data/shard1.users.json";
		ObjectMapper om   = new ObjectMapper();
		WebView      webview = new  WebView();
		final WebEngine webengine = webview.getEngine();
		
		
		try
		{
			req.url(endpoint);
			req.method("GET");
			
			res = http.send(req);
			
			if( res.getResponseCode() == 200 )
			{
				System.out.println("Status Code: " + res.getResponseCode());
				
				if( res.getStream() != null )
				{
					BufferedReader in = new BufferedReader( new InputStreamReader(res.getStream()) );
					String nextLine;
					StringBuilder builder = new StringBuilder();
					
					while( (nextLine = in.readLine()) != null )
					{
						builder.append(nextLine);
					}
					
					//System.out.println(builder.toString());
					Map<String,Object> jsonMap = om.readValue(builder.toString(), Map.class);
					
					//System.out.println("jsonMap: " + om.writeValueAsString(jsonMap) );
					List<String> keyList = new ArrayList<String>(jsonMap.keySet());
					String screepsEndpoint = "https://screeps.com/a/#!/profile/";
					
					for( String key : keyList )
					{
						//System.out.println("Key: " + key);
						//System.out.println("Value: " + om.writeValueAsString(jsonMap.get(key)));
						Map<String,Object> userMap = om.readValue(om.writeValueAsString(jsonMap.get(key)), Map.class);
						System.out.println("Username: " + userMap.get("username"));
						String tempEndpoint = screepsEndpoint + userMap.get("username");
						
						HttpRequest tempReq = new HttpRequest();
						tempReq.url(tempEndpoint);
						tempReq.method("GET");
						
						HttpResponse tempRes = http.send(tempReq);
						
						System.out.println("Status Code: " + tempRes.getResponseCode());
						
						in = new BufferedReader( new InputStreamReader(tempRes.getStream()) );
						builder = new StringBuilder();
						while( (nextLine = in.readLine()) != null )
						{
							builder.append(nextLine);
						}
						System.out.println(builder.toString());
						/*
						webengine.getLoadWorker().stateProperty().addListener(
					            new ChangeListener<State>() {
					                public void changed(ObservableValue ov, State oldState, State newState) {
					                    //if (newState == Worker.State.SUCCEEDED) 
					                	{
					                        Document doc = webengine.getDocument();
					                        //Serialize DOM
					                        OutputFormat format    = new OutputFormat (doc); 
					                        // as a String
					                        StringWriter stringOut = new StringWriter ();    
					                        XMLSerializer serial   = new XMLSerializer (stringOut, format);
					                        try {
					                            serial.serialize(doc);
					                        } catch (IOException e) {
					                            e.printStackTrace();
					                        }
					                        // Display the XML
					                        System.out.println(stringOut.toString());
					                    }
					                }
					            });
						*/
						
						break;
					}
				}
			}
			else
			{
				System.out.println("Status Code Error: " + res.getResponseCode());
			}
		}
		catch( Exception e )
		{
			System.out.println("Exception: " + e.getMessage());
		}		
	}
}
