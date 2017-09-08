/**
 * CacheManagerSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1-wso2v14  Built on : Jul 25, 2015 (11:19:54 IST)
 */
package ru.example.wso2.cache.api;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.ServiceLifeCycle;
import org.apache.axis2.service.Lifecycle;
import org.apache.log4j.LogManager;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ISet;

/**
 * CacheManagerSkeleton java skeleton for the axisService
 */
public class CacheManagerSkeleton implements ServiceLifeCycle, Lifecycle {

	protected static org.apache.log4j.Logger logger = LogManager
			.getLogger(CacheManagerSkeleton.class);

	protected static ISet<String> collectionsRegister;

	protected static boolean standaloneMode = false;

	protected static final String TARGET_NAMESPACE = "http://api.svyaznoy.ru/CacheManager";
	protected static final String TARGET_NAMESPACE_PREFIX = "cm";
	
	/**
	 * Auto generated method signature
	 * 
	 * @param getCollectionsList
	 * @return getCollectionsListResult
	 * @throws Exception
	 */

	public org.apache.axiom.om.OMElement getCollectionsList(
			org.apache.axiom.om.OMElement getCollectionsList) throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace namespace = factory.createOMNamespace(TARGET_NAMESPACE,
				TARGET_NAMESPACE_PREFIX);
		OMElement result = factory.createOMElement("getCollectionsListResult",
				namespace);
		try {
			Iterator<String> iterator = collectionsRegister.iterator();
			while (iterator.hasNext()) {
				OMElement collection = factory.createOMElement("collection",
						namespace);
				OMElement name = factory.createOMElement("name", namespace);
				OMElement count = factory.createOMElement("count", namespace);
				String key = (String) iterator.next();
				IMap<String, String> cache = Hazelcast
						.getAllHazelcastInstances().iterator().next()
						.getMap(key);
				name.setText(key);
				count.setText(new Integer(cache.size()).toString());
				collection.addChild(name);
				collection.addChild(count);
				result.addChild(collection);
			}
		} catch (Exception e) {
			logger.error("CacheManager: error in processing. ", e);
			throw e;
		}
		return result;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param putValue
	 * @return putValueResult
	 * @throws Exception
	 */

	public org.apache.axiom.om.OMElement putValue(
			org.apache.axiom.om.OMElement putValue) throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace namespace = factory.createOMNamespace(TARGET_NAMESPACE,
				TARGET_NAMESPACE_PREFIX);
		OMElement result = factory.createOMElement("putValueResult", namespace);
		OMElement resultElement = factory.createOMElement("result", namespace);
		try {
			OMElement cacheIdentOM = (OMElement) putValue
					.getChildrenWithLocalName("cacheIdent").next();
			OMElement keyOM = (OMElement) putValue.getChildrenWithLocalName(
					"key").next();
			OMElement valueOM = (OMElement) putValue.getChildrenWithLocalName(
					"value").next();
			OMElement TTLOM = (OMElement) putValue.getChildrenWithLocalName(
					"TTL").next();
			IMap<String, String> actualCollection = getOrCreateCollectionByName(cacheIdentOM
					.getText());
			if(TTLOM.getText()==null||TTLOM.getText().equals("")){
				throw new IllegalArgumentException("TTL parameter must be defined!!!");
			}
			actualCollection.put(keyOM.getText(), valueOM.getText(),
					Integer.parseInt(TTLOM.getText()), TimeUnit.SECONDS);
			resultElement.setText("Success");
			result.addChild(resultElement);
		} catch (Exception e) {
			logger.error("CacheManager: error in processing. ", e);
			throw e;
		}
		return result;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param removeCollection
	 * @return removeCollectionResult
	 * @throws Exception
	 */

	public org.apache.axiom.om.OMElement removeCollection(
			org.apache.axiom.om.OMElement removeCollection) throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace namespace = factory.createOMNamespace(TARGET_NAMESPACE,
				TARGET_NAMESPACE_PREFIX);
		OMElement result = factory.createOMElement("removeCollectionResult",
				namespace);
		OMElement resultElement = factory.createOMElement("result", namespace);
		try {
			OMElement cacheIdentOM = (OMElement) removeCollection
					.getChildrenWithLocalName("cacheIdent").next();
			IMap<String, String> actualCollection;
			if (collectionsRegister.contains(cacheIdentOM.getText())) {
				actualCollection = Hazelcast.getAllHazelcastInstances()
						.iterator().next().getMap(cacheIdentOM.getText());
				if (actualCollection != null) {
					actualCollection.clear();
					actualCollection.destroy();
					collectionsRegister.remove(cacheIdentOM.getText());
					resultElement.setText("Success");
				} else {
					resultElement.setText("NotFound");
				}
			} else {
				resultElement.setText("NotFound");
			}
		} catch (Exception e) {
			logger.error("CacheManager: error in processing. ", e);
			throw e;
		}

		result.addChild(resultElement);
		return result;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param getValue
	 * @return getValueResult
	 * @throws Exception
	 */

	public org.apache.axiom.om.OMElement getValue(
			org.apache.axiom.om.OMElement getValue) throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace namespace = factory.createOMNamespace(TARGET_NAMESPACE,
				TARGET_NAMESPACE_PREFIX);
		OMElement result = factory.createOMElement("getValueResult", namespace);
		OMElement resultElement = factory.createOMElement("result", namespace);
		try {
			OMElement cacheIdentOM = (OMElement) getValue
					.getChildrenWithLocalName("cacheIdent").next();
			OMElement keyOM = (OMElement) getValue.getChildrenWithLocalName(
					"key").next();
			logger.debug(String.format(
					"CacheManager: received values cache: %s key: %s",
					cacheIdentOM.getText(), keyOM.getText()));
			IMap<String, String> actualCollection = getOrCreateCollectionByName(cacheIdentOM
					.getText());
			if (actualCollection.containsKey(keyOM.getText())) {
				logger.debug("CacheManager: found");
				resultElement.setText(actualCollection.get(keyOM.getText()));
			} else {
				resultElement.setText("NotFound");
			}
			result.addChild(resultElement);
		} catch (Exception e) {
			logger.error("CacheManager: error in processing. ", e);
			throw e;
		}
		return result;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param clearCollection
	 * @return clearCollectionResult
	 * @throws Exception
	 */

	public org.apache.axiom.om.OMElement clearCollection(
			org.apache.axiom.om.OMElement clearCollection) throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace namespace = factory.createOMNamespace(TARGET_NAMESPACE,
				TARGET_NAMESPACE_PREFIX);
		OMElement result = factory.createOMElement("clearCollectionResult",
				namespace);
		OMElement resultElement = factory.createOMElement("result", namespace);
		try {
			OMElement cacheIdentOM = (OMElement) clearCollection
					.getChildrenWithLocalName("cacheIdent").next();
			IMap<String, String> actualCollection;
			if (collectionsRegister.contains(cacheIdentOM.getText())) {
				actualCollection = Hazelcast.getAllHazelcastInstances()
						.iterator().next().getMap(cacheIdentOM.getText());
				if (actualCollection != null) {
					actualCollection.clear();
					resultElement.setText("Success");
				} else {
					resultElement.setText("NotFound");
				}
			} else {
				resultElement.setText("NotFound");
			}
			result.addChild(resultElement);
		} catch (Exception e) {
			logger.error("CacheManager: error in processing. ", e);
			throw e;
		}
		return result;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param removeKey
	 * @return removeKeyResult
	 * @throws Exception
	 */

	public org.apache.axiom.om.OMElement removeKey(
			org.apache.axiom.om.OMElement removeKey) throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace namespace = factory.createOMNamespace(TARGET_NAMESPACE,
				TARGET_NAMESPACE_PREFIX);
		OMElement result = factory
				.createOMElement("removeKeyResult", namespace);
		OMElement resultElement = factory.createOMElement("result", namespace);
		try {
			OMElement cacheIdentOM = (OMElement) removeKey
					.getChildrenWithLocalName("cacheIdent").next();
			OMElement keyOM = (OMElement) removeKey.getChildrenWithLocalName(
					"key").next();
			IMap<String, String> actualCollection;
			if (collectionsRegister.contains(cacheIdentOM.getText())) {
				actualCollection = Hazelcast.getAllHazelcastInstances()
						.iterator().next().getMap(cacheIdentOM.getText());
				if (actualCollection != null
						&& actualCollection.containsKey(keyOM.getText())) {
					actualCollection.delete(keyOM.getText());
					resultElement.setText("Success");
				} else {
					resultElement.setText("NotFound");
				}
			} else {
				resultElement.setText("NotFound");
			}
			result.addChild(resultElement);
		} catch (Exception e) {
			logger.error("CacheManager: error in processing. ", e);
			throw e;
		}
		return result;
	}

	/**
	 * This method used for extend functionality
	 * 
	 * @param command
	 * @return commandResult
	 */

	public org.apache.axiom.om.OMElement command(
			org.apache.axiom.om.OMElement command) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException(
				"CacheManager: Please implement " + this.getClass().getName()
						+ "#command");
	}

	/**
	 * LifeCycle method Used for clear cache collections before undeploy
	 */
	public void shutDown(ConfigurationContext arg0, AxisService arg1) {
		logger.debug("CacheManager: shutdown service.. mode " + standaloneMode);
		if (collectionsRegister != null) {
			try {
				Iterator<String> iterator = collectionsRegister.iterator();
				while (iterator.hasNext()) {
					IMap<String, String> map = Hazelcast
							.getAllHazelcastInstances().iterator().next()
							.getMap(iterator.next());
					map.clear();
					map.destroy();
				}
				collectionsRegister.clear();
				collectionsRegister.destroy();
			} catch (Exception e) {
				logger.error("CacheManager: can't destroy cache", e);
			}
		}
		if (standaloneMode) {
			try {

				HazelcastInstance instance = Hazelcast
						.getHazelcastInstanceByName("CacheManager");
				logger.debug("CacheManager: find instance");
				if (instance != null) {
					logger.debug("CacheManager: shutdown instance..");
					Hazelcast.getHazelcastInstanceByName("CacheManager")
							.getLifecycleService().shutdown();
					logger.debug("CacheManager: shutdown instance completed..");
				} else {
					logger.debug("CacheManager: instance not found..");
				}
			} catch (Exception e) {
				logger.error("CacheManager: Can't shutdown instance. ", e);
			}
		}
	}

	/**
	 * LifeCycle method
	 */
	public void startUp(ConfigurationContext arg0, AxisService arg1) {
		logger.debug("CacheManager: start up..");
	}

	/**
	 * LifeCycle method
	 */
	public void destroy(ServiceContext arg0) {
	}

	/**
	 * LifeCycle method Used for initialize cache collections before first start
	 */
	public void init(ServiceContext arg0) throws AxisFault {
		if (collectionsRegister == null) {
			try {
				logger.debug("CacheManager: try to init cache..");
				logger.debug("CacheManager: get instance..");
				if (Hazelcast.getAllHazelcastInstances().isEmpty()) {
					logger.debug("CacheManager: standalone mode..");
					Config config = new Config();
					config.getNetworkConfig().setPortAutoIncrement(true);
					config.setInstanceName("CacheManager");
					HazelcastInstance instance = Hazelcast
							.newHazelcastInstance(config);
					standaloneMode = true;
					logger.debug("CacheManager: created new instance with name "
							+ (instance.getName() == null ? "null" : instance
									.getName()));
				}
				HazelcastInstance instance = Hazelcast
						.getAllHazelcastInstances().iterator().next();
				logger.debug("CacheManager: create cache register..");
				collectionsRegister = instance.getSet("collectionsRegister");
				logger.debug("CacheManager: cache init completed");
			} catch (Exception e) {
				logger.error("CacheManager: failed to init cache: " + e);
			}
		}
	}

	/**
	 * 
	 * @param name
	 * @return new collection
	 */
	private IMap<String, String> getOrCreateCollectionByName(String name) {
		IMap<String, String> newCollection = Hazelcast
				.getAllHazelcastInstances().iterator().next().getMap(name);
		if (!collectionsRegister.contains(name)) {
			collectionsRegister.add(name);
			return newCollection;
		}
		return newCollection;
	}
	/**
	 * utility method for clear instances
	 */
	/*
	 * private void closeAllInstances(){ Iterator<HazelcastInstance> iter =
	 * Hazelcast.getAllHazelcastInstances().iterator(); while(iter.hasNext()){
	 * HazelcastInstance instance = (HazelcastInstance)iter.next();
	 * logger.debug("CacheManager: close instance with name " +
	 * (instance.getName()==null?"null":instance.getName()));
	 * instance.getLifecycleService().shutdown(); } }
	 */

}
