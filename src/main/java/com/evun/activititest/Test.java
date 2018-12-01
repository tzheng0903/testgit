package com.evun.activititest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {

	public static class Node{
		private String id;
		private String parentId;
		private List<Node> list;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getParentId() {
			return parentId;
		}
		public void setParentId(String parentId) {
			this.parentId = parentId;
		}
		public List<Node> getList() {
			return list;
		}
		public void setList(List<Node> list) {
			this.list = list;
		}
		public Node(String id, String parentId) {
			super();
			this.id = id;
			this.parentId = parentId;
		}
		@Override
		public String toString() {
			return "Node [id=" + id + ", parentId=" + parentId + ", list=" + list + "]";
		}
		
		
	}
	
	public static class Nodex{
		private long id;
		private long pid;
		private List<Nodex> listx;
		public Nodex(long id, long pid) {
			super();
			this.id = id;
			this.pid = pid;
		}
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public long getPid() {
			return pid;
		}
		public void setPid(long pid) {
			this.pid = pid;
		}
		public List<Nodex> getListx() {
			return listx;
		}
		public void setListx(List<Nodex> listx) {
			this.listx = listx;
		}
		
	}
	
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, JsonProcessingException {
		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node("2.1","2"));
		nodes.add(new Node("2.2","2"));
		nodes.add(new Node("2.3","2"));
		nodes.add(new Node("3.1","3"));
		nodes.add(new Node("3.1.1","3.1"));
		nodes.add(new Node("3.1.2","3.1"));
		nodes.add(new Node("2.1.1","2.1"));
		nodes.add(new Node("2.1.2","2.1"));
		nodes.add(new Node("3","0"));
		nodes.add(new Node("2","0"));
		Collection<Node> tree = genTree(nodes,Node.class,"id","parentId","list");
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(tree);
		
		String s = mapper.writeValueAsString(tree);
		System.err.println(s);
		
		List<Nodex> nodexs = new ArrayList<>();
		nodexs.add(new Nodex(11,1));
		nodexs.add(new Nodex(12,1));
		nodexs.add(new Nodex(1,0));
		nodexs.add(new Nodex(2,0));
		nodexs.add(new Nodex(21,2));
		nodexs.add(new Nodex(22,2));
		nodexs.add(new Nodex(23,2));
		System.out.println(mapper.writeValueAsString(genTree(nodexs,Nodex.class,"id","pid","listx")));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> genTree(List<T> collection,Class<T> clz,/*父节点的映射字段名称*/String keyName,/*子节点对应到父节点的字段名称*/String subKeyName,String subCollectionName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		Field keyField = clz.getDeclaredField(keyName);
		Field subKeyField = clz.getDeclaredField(subKeyName);
		Field subCollectionField = clz.getDeclaredField(subCollectionName);

		List<T> tree = new ArrayList<>();
		
		keyField.setAccessible(true);
		subKeyField.setAccessible(true);
		subCollectionField.setAccessible(true);
		for (int i = 0;i<collection.size();i++) {
			T obj = collection.get(i);
			boolean topFlag = true;
			Object subKeyValue = subKeyField.get(obj);
			if(subKeyValue != null){
				for (int j = 0;j<collection.size();j++) {
					T objx = collection.get(j);
					if(subKeyValue.equals(keyField.get(objx))){
						topFlag = false;
						Object subCollectionObject = subCollectionField.get(objx);
						List<T> subCollection = null;
						if(subCollectionObject == null){
							subCollection = new ArrayList<>();
							subCollectionField.set(objx, subCollection);
						}else{
							subCollection = (List<T>) subCollectionObject;
						}
						subCollection.add(obj);
						continue;
					}
				}
			}
			if(topFlag){
				tree.add(obj);
			}
		}
		return tree;
	}
}
