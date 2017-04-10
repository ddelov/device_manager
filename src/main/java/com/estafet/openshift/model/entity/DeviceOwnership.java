package com.estafet.openshift.model.entity;

import org.apache.log4j.Logger;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.estafet.openshift.config.Constants.*;

/**
 * Created by Delcho Delov on 9.3.2017 г..
 */
@Entity
@Table(name=TABLE_NAME_DEVICE_OWNERSHIP, schema=SCHEMA_NAME)
public class DeviceOwnership {
		private static Logger log= Logger.getLogger(DeviceOwnership.class);
		@Id
		@GeneratedValue
		@Column(name = COL_ID, nullable = false)
		private int id;

		@Column(name = COL_CUST_ID,nullable = false)
//		@ManyToOne
//		private Customer userId;// FK to Users
		private int customerId;// FK to Users

		@Column(name = COL_THING_NAME,nullable = false)
		private String thingName; //thingName
		@Column(name = COL_THING_TYPE,nullable = false)
		private String thingTypeName;
		@Column(name = COL_SN,nullable = false)
		private String sn;
		@Column(name = COL_OWN,nullable = false)
		private boolean own;
		@Column(name = COL_VALID_FROM,nullable = false)
		private String validFrom; // date in format 'yyyyMMdd'
		@Column(name = COL_VALID_TO,nullable = true)
		private String validTo; // date in format 'yyyyMMdd'

		public DeviceOwnership() {//for JSON purposes
		}

		public DeviceOwnership(int customerId, String thingName, String thingTypeName, String sn, boolean own) {
				this.customerId = customerId;
				this.thingName = thingName;
				this.thingTypeName = thingTypeName;
				this.sn = sn;
				this.own = own;
		}

		public DeviceOwnership(String thingName) {
				this.thingName = thingName;
		}

		public int getId() {
				return id;
		}

		public void setId(int id) {
				this.id = id;
		}

		public int getCustomerId() {
				return customerId;
		}

		public String getThingName() {
				return thingName;
		}

		public boolean isOwn() {
				return own;
		}

		public String getValidFrom() {
				return validFrom;
		}

		public String getValidTo() {
				return validTo;
		}

		public void setValidFrom(Calendar calendar) {
				if(calendar!=null){
						final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
						sdf.setTimeZone(calendar.getTimeZone());
						this.validFrom = sdf.format(calendar.getTime());
				}
		}
		public void setValidTo(Calendar calendar) {
				if(calendar!=null){
						final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
						sdf.setTimeZone(calendar.getTimeZone());
						this.validTo = sdf.format(calendar.getTime());
				}
		}

		public String getThingTypeName() {
				return thingTypeName;
		}

		public String getSn() {
				return sn;
		}

		/**
		 * Search last active record fi device ONLY BY THING ID. If no such record exists returns false. If DR returns
		 * more than 1 record, this is indication to error
		 *
		 * @return true if record is loaded from DB, false-no such record
		 */
//		public boolean loadLastActive(Connection conn) throws DMException, SQLException {
//				if (conn == null || conn.isClosed()) {
//						throw new EmptyArgumentException("connection");
//				}
//
////				Calendar today = Calendar.getInstance();
////				today.set(Calendar.HOUR_OF_DAY, 0);
////				today.set(Calendar.MINUTE, 0);
////				today.set(Calendar.SECOND, 0);
////				today.set(Calendar.MILLISECOND, 0);
////				final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
////				sdf.setTimeZone(today.getTimeZone());
////
////				Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
////				eav.put(":today", new AttributeValue().withS(String.valueOf(sdf.format(today.getTime()))));
////				eav.put(":deviceId", new AttributeValue().withS(getThingName()));
////
////				StringBuilder sb = new StringBuilder();
////				sb.append("( ( ").append("attribute_not_exists(").append(COL_VALID_TO).append(") AND ").append(COL_VALID_FROM).append(" <= :today").append(" ) ");
////				sb.append(" OR ");
////				sb.append(" ( attribute_exists(").append(COL_VALID_TO).append(") AND ").append(COL_VALID_TO).append(" > :today").append(" ) )");
////				sb.append(" AND ").append(COL_THING_NAME).append(" = :deviceId");
////				DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
////								.withFilterExpression(sb.toString())
////								.withExpressionAttributeValues(eav);
////				final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()/*.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))*/.build();
////				DynamoDBMapper mapper = new DynamoDBMapper(client);//getDynamoDBMapper();
//				List<DeviceOwnership> records = getScanResults();
//				if(records==null || records.isEmpty()){
//						return false;
//				}
//				// expected 1 record
//				if(records.size()>1){
//						throw new DMException("More than 1 active record found for device "+ getThingName());
//				}
//				//===============
//				final DeviceOwnership deo = records.get(0);
//				setValidFrom(deo.getValidFrom());
//				setThingTypeName(deo.thingTypeName);
//				setId(deo.getId());
//				setValidTo(deo.getValidTo());
//				setCustomerId(deo.getCustomerId());
//				setOwn(deo.isOwn());
//				setSn(deo.getSn());
//				return true;
//		}

//		public void writeToDb(Connection conn) throws DMException, SQLException {
//				if (conn == null || conn.isClosed()) {
//						throw new EmptyArgumentException("connection");
//				}
//
//				//TODO write Postgres implementation
////				Table table = getDynamoDBTable();// only for test purposes
////				try {
////						final Item item = new Item()
////										.withPrimaryKey(COL_ID, getId()!=null?getId():UUID.randomUUID().toString()) // Every item gets a unique id
////										.withString(COL_CUST_ID, getCustomerId())
////										.withString(COL_THING_NAME, getThingName())
////										.withString(COL_THING_TYPE, getThingTypeName())
////										.withString(COL_SN, getSn())
////										.withString(COL_VALID_FROM, getValidFrom())
////										.withBoolean(COL_OWN, isOwn());
////						if(!Utils.isEmpty(getValidTo())) {
////								item.withString(COL_VALID_TO, getValidTo());
////						}
////						table.putItem(item);
////				}catch (Exception e){
////						log.error("Could not store "+ TABLE_NAME_DEVICE_OWNERSHIP + " record in the DB", e);
////						throw new CentricaException("Could not store "+ TABLE_NAME_DEVICE_OWNERSHIP + " record in the DB", e);
////				}
//		}
		protected List<DeviceOwnership> getScanResults() {
				//TODO write Postgres implementation
				return null;
		}

//		protected Table getDynamoDBTable() {
//				final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
//				DynamoDB dynamoDB = new DynamoDB(client);
//				return dynamoDB.getTable(TABLE_NAME_DEVICE_OWNERSHIP);
//		}
//		protected DynamoDBMapper getDynamoDBMapper(){
//				final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()/*.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))*/.build();
//				return new DynamoDBMapper(client);
//		}

		public void setCustomerId(int customerId) {
				this.customerId = customerId;
		}

		public void setThingName(String thingName) {
				this.thingName = thingName;
		}

		public void setThingTypeName(String thingTypeName) {
				this.thingTypeName = thingTypeName;
		}

		public void setSn(String sn) {
				this.sn = sn;
		}

		public void setOwn(boolean own) {
				this.own = own;
		}

		public void setValidFrom(String validFrom) {
				this.validFrom = validFrom;
		}

		public void setValidTo(String validTo) {
				this.validTo = validTo;
		}

		public Map<String, Object> asMap() {
				Map<String, Object> res = new HashMap<>(8);
				res.put(COL_ID, getId());
				res.put(COL_CUST_ID, getCustomerId());
				res.put(COL_THING_NAME, getThingName());
				res.put(COL_THING_TYPE, getThingTypeName());
				res.put(COL_SN, getSn());
				res.put(COL_OWN, isOwn());
				res.put(COL_VALID_FROM, getValidFrom());
				res.put(COL_VALID_TO, getValidTo());
				return res;
		}

}
