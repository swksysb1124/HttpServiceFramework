package http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SimpleURLConfigManager 
	implements URLConfigManager {
	
	private static SimpleURLConfigManager instance;
	private static final String FILENAME = "url_example.xml";
	
	public static SimpleURLConfigManager getInstance() {
		if(instance == null) {
			synchronized(SimpleURLConfigManager.class) {
				if(instance == null) {
					instance = new SimpleURLConfigManager();
				}
			}
		}
		return instance;
	}
	
	private final List<URLInfo> urlList = new ArrayList<>();
	
	private SimpleURLConfigManager() {}
	
    @Override
    public URLInfo findURL(final String findKey) {
        if(urlList == null || urlList.isEmpty()) {
            fetchUrlDataFromXml();
        }
        
        for(URLInfo data: urlList) {
            if(findKey.equals(data.getKey())) {
                return data;
            }
        }
        return null;
    }
    
    @Override
    public void fetchUrlDataFromXml() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream is = new FileInputStream(new File(FILENAME));
            Document doc = dBuilder.parse(is); // 以樹狀格式儲存在記憶體中
            NodeList nList = doc.getElementsByTagName("url");
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                NamedNodeMap attr = node.getAttributes();
                if (attr != null) {
                    String key = attr.getNamedItem("key").getNodeValue();
                    long expires = Long.parseLong(attr.getNamedItem("expires").getNodeValue());
                    String method = attr.getNamedItem("method").getNodeValue();
                    String scheme = attr.getNamedItem("scheme").getNodeValue();
                    String host = attr.getNamedItem("host").getNodeValue();
                    String path = attr.getNamedItem("path").getNodeValue();
                    urlList.add(new URLInfo(key, expires, method, scheme, host, path));
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
