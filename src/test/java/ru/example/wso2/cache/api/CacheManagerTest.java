package ru.example.wso2.cache.api;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axis2.description.AxisService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hazelcast.core.Hazelcast;

public class CacheManagerTest  {

	private static CacheManagerSkeleton cacheManager = new CacheManagerSkeleton();

	@BeforeClass
	public static void beforeClass() throws Exception {
		System.out.println("starting..");
		cacheManager.startUp(null, null);
		cacheManager.init(null);
	}

	@AfterClass
	public static void afterClass() throws Exception {
		cacheManager.destroy(null);
		cacheManager.shutDown(null, null);
	}

	@Test
	public void ensureThatCreatedNewInstanceInStandaloneMode() throws Exception {
		assertEquals(false, Hazelcast.getAllHazelcastInstances().isEmpty());
	}

	@Test
	public void ensureThatPutValueReturnsSuccess() throws Exception {
		OMElement input = createPutMessage();
		try {
			OMElement result = cacheManager.putValue(input);
			OMElement message = (OMElement) result.getChildren().next();
			assertEquals("Success", message.getText());
			OMElement inputRemoveCollection = createRemoveCollectionMessage();
			OMElement resultRemoveCollection = cacheManager
					.removeCollection(inputRemoveCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void ensureThatMapInRegisterAndInHazelcast() throws Exception {
		OMElement input = createPutMessage();
		try {
			OMElement result = cacheManager.putValue(input);
			assertEquals(true,
					cacheManager.collectionsRegister.contains("testcache"));
			OMElement inputRemove = createRemoveCollectionMessage();
			OMElement resultremove = cacheManager.removeCollection(inputRemove);
			OMElement inputRemoveCollection = createRemoveCollectionMessage();
			OMElement resultRemoveCollection = cacheManager
					.removeCollection(inputRemoveCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void ensureThatValueInMap() throws Exception {
		OMElement input = createPutMessage();
		try {
			OMElement result = cacheManager.putValue(input);
			assertEquals(true, Hazelcast.getAllHazelcastInstances().iterator()
					.next().getMap("testcache").containsKey("k2"));
			OMElement inputRemoveCollection = createRemoveCollectionMessage();
			OMElement resultRemoveCollection = cacheManager
					.removeCollection(inputRemoveCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void ensureThatGetValueReturnsCorrectResult() throws Exception {
		OMElement inputPut = createPutMessage();
		OMElement inputGet = createGetMessage();
		try {
			OMElement resultPut = cacheManager.putValue(inputPut);
			OMElement resultGet = cacheManager.getValue(inputGet);
			OMElement message = (OMElement) resultGet.getChildren().next();
			assertEquals("1", message.getText());
			OMElement inputRemoveCollection = createRemoveCollectionMessage();
			OMElement resultRemoveCollection = cacheManager
					.removeCollection(inputRemoveCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void ensureThatRemoveKeyWorked() throws Exception {
		OMElement inputPut = createPutMessage();
		OMElement inputRemove = createRemoveMessage();
		try {
			OMElement resultPut = cacheManager.putValue(inputPut);
			OMElement resultRemove = cacheManager.removeKey(inputRemove);
			OMElement message = (OMElement) resultRemove.getChildren().next();
			assertEquals("Success", message.getText());
			assertEquals(false, Hazelcast.getAllHazelcastInstances().iterator()
					.next().getMap("testcache").containsKey("k2"));
			OMElement inputRemoveCollection = createRemoveCollectionMessage();
			OMElement resultRemoveCollection = cacheManager
					.removeCollection(inputRemoveCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void ensureThatRemoveCollectionWorked() throws Exception {
		OMElement inputPut = createPutMessage();
		OMElement inputRemove = createRemoveCollectionMessage();
		try {
			OMElement resultPut = cacheManager.putValue(inputPut);
			OMElement resultRemove = cacheManager.removeCollection(inputRemove);
			OMElement message = (OMElement) resultRemove.getChildren().next();
			assertEquals("Success", message.getText());
			assertEquals(false,
					cacheManager.collectionsRegister.contains("testcache"));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void ensureThatClearCollectionWorked() throws Exception {
		OMElement inputPut = createPutMessage();
		OMElement inputClear = createClearCollectionMessage();
		try {
			OMElement resultPut = cacheManager.putValue(inputPut);
			OMElement resultClear = cacheManager.clearCollection(inputClear);
			OMElement message = (OMElement) resultClear.getChildren().next();
			assertEquals("Success", message.getText());
			assertEquals(true,
					cacheManager.collectionsRegister.contains("testcache"));
			assertEquals(false, Hazelcast.getAllHazelcastInstances().iterator()
					.next().getMap("testcache").containsKey("k2"));
			OMElement inputRemoveCollection = createRemoveCollectionMessage();
			OMElement resultRemoveCollection = cacheManager
					.removeCollection(inputRemoveCollection);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void ensureThatGetCollectionListWorkedCorrectly() throws Exception {
		OMElement inputPut1 = createPutMessage("k1", "testcache1");
		OMElement inputPut2 = createPutMessage("k2", "testcache2");
		OMElement inputPut3 = createPutMessage("k3", "testcache2");

		OMElement inputGetList = createGetCollectionListMessage();
		try {
			OMElement resultPut1 = cacheManager.putValue(inputPut1);
			OMElement resultPut2 = cacheManager.putValue(inputPut2);
			OMElement resultPut3 = cacheManager.putValue(inputPut3);
			OMElement resultGetList = cacheManager
					.getCollectionsList(inputGetList);
			assertEquals(true, resultGetList.getChildren().hasNext());
			int collectionsCount = 0;
			Iterator<OMElement> iterator = resultGetList.getChildren();
			while (iterator.hasNext()) {
				collectionsCount++;
				OMElement collectionInfo = (OMElement) iterator.next();
				OMElement name = (OMElement) collectionInfo
						.getChildrenWithLocalName("name").next();
				OMElement count = (OMElement) collectionInfo
						.getChildrenWithLocalName("count").next();
				if (name.getText() == "testcache1") {
					assertEquals(1, count.getText());
					collectionsCount++;
				} else if (name.getText() == "testcache2") {
					assertEquals(2, count.getText());
					collectionsCount++;
				} 
			}
			assertEquals(2, collectionsCount);
			OMElement inputRemoveCollection1 = createRemoveCollectionMessage("testcache1");
			OMElement resultRemoveCollection1 = cacheManager
					.removeCollection(inputRemoveCollection1);
			OMElement inputRemoveCollection2 = createRemoveCollectionMessage("testcache2");
			OMElement resultRemoveCollection2 = cacheManager
					.removeCollection(inputRemoveCollection2);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	public Object getServiceObject(AxisService theService) {
		String service = theService.getName();

		if (service.equals("CacheManager")) {
			return cacheManager;
		}
		return null;
	}


	private OMElement createPutMessage() {
		String inputString = "<cac:putValue xmlns:cac=\"http://api.svyaznoy.ru/CacheManager\">"
				+ "<!--type: string-->"
				+ "<cac:cacheIdent>testcache</cac:cacheIdent>"
				+ "<!--type: string-->"
				+ "<cac:key>k2</cac:key>"
				+ "<!--type: string-->"
				+ "<cac:value>1</cac:value>"
				+ "<!--type: string-->"
				+ "<cac:TTL>300</cac:TTL>"
				+ "</cac:putValue>";
		OMElement input = OMXMLBuilderFactory.createOMBuilder(
				new ByteArrayInputStream(inputString.getBytes()))
				.getDocumentElement();
		return input;

	}

	private OMElement createPutMessage(String key, String cacheIdent) {
		String inputString = String.format(
				"<cac:putValue xmlns:cac=\"http://api.svyaznoy.ru/CacheManager\">"
						+ "<!--type: string-->"
						+ "<cac:cacheIdent>%s</cac:cacheIdent>"
						+ "<!--type: string-->" + "<cac:key>%s</cac:key>"
						+ "<!--type: string-->" + "<cac:value>1</cac:value>"
						+ "<!--type: string-->" + "<cac:TTL>300</cac:TTL>"
						+ "</cac:putValue>", cacheIdent, key);
		OMElement input = OMXMLBuilderFactory.createOMBuilder(
				new ByteArrayInputStream(inputString.getBytes()))
				.getDocumentElement();
		return input;

	}

	private OMElement createGetMessage() {
		String inputString = "<cac:getValue xmlns:cac=\"http://api.svyaznoy.ru/CacheManager\">"
				+ "<!--type: string-->"
				+ "<cac:cacheIdent>testcache</cac:cacheIdent>"
				+ "<!--type: string-->"
				+ "<cac:key>k2</cac:key>"
				+ "</cac:getValue>";
		OMElement input = OMXMLBuilderFactory.createOMBuilder(
				new ByteArrayInputStream(inputString.getBytes()))
				.getDocumentElement();
		return input;

	}

	private OMElement createRemoveMessage() {
		String inputString = "<cac:removeKey xmlns:cac=\"http://api.svyaznoy.ru/CacheManager\">"
				+ "<!--type: string-->"
				+ "<cac:cacheIdent>testcache</cac:cacheIdent>"
				+ "<!--type: string-->"
				+ "<cac:key>k2</cac:key>"
				+ "</cac:removeKey>";
		OMElement input = OMXMLBuilderFactory.createOMBuilder(
				new ByteArrayInputStream(inputString.getBytes()))
				.getDocumentElement();
		return input;
	}

	private OMElement createRemoveCollectionMessage() {
		String inputString = "<cac:removeCollection xmlns:cac=\"http://api.svyaznoy.ru/CacheManager\">"
				+ "<!--type: string-->"
				+ "<cac:cacheIdent>testcache</cac:cacheIdent>"
				+ "</cac:removeCollection>";
		OMElement input = OMXMLBuilderFactory.createOMBuilder(
				new ByteArrayInputStream(inputString.getBytes()))
				.getDocumentElement();
		return input;
	}

	private OMElement createRemoveCollectionMessage(String cacheIdent) {
		String inputString = String.format("<cac:removeCollection xmlns:cac=\"http://api.svyaznoy.ru/CacheManager\">"
				+ "<!--type: string-->"
				+ "<cac:cacheIdent>%s</cac:cacheIdent>"
				+ "</cac:removeCollection>",cacheIdent);
		OMElement input = OMXMLBuilderFactory.createOMBuilder(
				new ByteArrayInputStream(inputString.getBytes()))
				.getDocumentElement();
		return input;
	}

	private OMElement createClearCollectionMessage() {
		String inputString = "<cac:clearCollection xmlns:cac=\"http://api.svyaznoy.ru/CacheManager\">"
				+ "<!--type: string-->"
				+ "<cac:cacheIdent>testcache</cac:cacheIdent>"
				+ "</cac:clearCollection>";
		OMElement input = OMXMLBuilderFactory.createOMBuilder(
				new ByteArrayInputStream(inputString.getBytes()))
				.getDocumentElement();
		return input;
	}

	private OMElement createGetCollectionListMessage() {
		String inputString = "<cac:getCollectionsList xmlns:cac=\"http://api.svyaznoy.ru/CacheManager\">"
				+ "</cac:getCollectionsList>";
		OMElement input = OMXMLBuilderFactory.createOMBuilder(
				new ByteArrayInputStream(inputString.getBytes()))
				.getDocumentElement();
		return input;
	}
}
