package com.chenfeng.ssoserver.beans;

import java.util.ArrayList;
import java.util.List;

import com.chenfeng.ssoserver.model.Apps;
import com.chenfeng.ssoserver.model.User;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * 获取客户端系统列表
 * 
 * @author Administrator
 *
 */
public class AppConfigBean implements ResourceLoaderAware {

	private static Logger logger = LoggerFactory.getLogger(AppConfigBean.class);

	private ResourceLoader resourceLoader;

	private List<Apps> apps = new ArrayList<Apps>();

	// 获取客户端系统列表
	public void init() throws Exception {
		try {
			loadApps();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("加载apps.xml失败");
		}
	}

	private void loadApps() throws Exception {
		Resource resource = resourceLoader.getResource("classpath:apps.xml");
		SAXReader reader = new SAXReader();
		Document doc = reader.read(resource.getInputStream());
		
		Element rootElement = doc.getRootElement();
		List<Element> appElements = rootElement.elements();

		apps.clear();
		for (Element element : appElements) {
			Apps app = new Apps();
			app.setId(element.attributeValue("id"));
			app.setName(element.attributeValue("name"));
			app.setBaseUrl(element.elementText("baseUrl"));
			app.setHomeUri(element.elementText("homeUri"));
			app.setInnerAddress(element.elementText("innerAddress"));

			apps.add(app);
		}
	}

	public void destroy() {
		for (Apps apps : apps ) {
			apps.noticeShutdown();
		}
	}

	public List<Apps> getApps() {
		return apps;
	}

	public void setApps(List<Apps> apps) {
		this.apps = apps;
	}

	/**
	 * 获取用户授权系统列表
	 * 
	 * @param loginUser
	 * @return
	 * @throws Exception
	 */
	public List<Apps> getAuthedApps(User loginUser)
			throws Exception {
		return apps;
	}

	@Override
	public void setResourceLoader(ResourceLoader loader) {
		this.resourceLoader = loader;
	}

}
