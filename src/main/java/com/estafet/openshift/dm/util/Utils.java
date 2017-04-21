package com.estafet.openshift.dm.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Delcho Delov on 06.04.17.
 *
 */
public class Utils {
		private Utils() {
		}

		public static boolean isEmpty(String val) {
				return val == null || val.trim().isEmpty();
		}

		public static boolean areEquals(double a, double b, double delta) {
				if (a == b) {
						return true;
				}
				if (a > b) {
						return a - b < delta;
				}
				return b - a < delta;
		}
		public static int sendPutRequest(String url, String deviceId) throws IOException {
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpPut httpPut = new HttpPut(url+"/"+deviceId);
				httpPut.setHeader("Content-type", "application/json");
				httpPut.setHeader("Accept", "application/json");
				CloseableHttpResponse response = httpClient.execute(httpPut);
				BufferedReader rd = new BufferedReader(new InputStreamReader(
								response.getEntity().getContent()));
				String line = rd.readLine();
				System.out.println(line);
				return response.getStatusLine().getStatusCode();
		}

		public static int sendDeleteRequest(String url, String deviceId) throws IOException {
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpDelete httpDelete = new HttpDelete(url+"/"+deviceId);
				httpDelete.setHeader("Content-type", "application/json");
				httpDelete.setHeader("Accept", "application/json");
				HttpResponse response = httpClient.execute(httpDelete);
				BufferedReader rd = new BufferedReader(new InputStreamReader(
								response.getEntity().getContent()));
				String line = rd.readLine();
				System.out.println(line);
				return response.getStatusLine().getStatusCode();
		}

//		public static void main(String[] args) throws IOException {
//				final String url = "http://device-manager-device-manager.192.168.42.182.nip.io/registerDevice";
//				final String payload = "{" +
//								"\"customer_id\":\"Asan@ibm.bg\"," +
//								"\"thing_name\":\"Pod1\"," +
//								"\"thing_type\":\"подводница\"," +
//								"\"sn\":\"BG23-445\"," +
//								"\"own\":true," +
//								"\"valid_from\":\"20170311\"" +
//								"}";
//				final int responseCode = makePostJsonRequest(url, payload);
//				System.out.println("responseCode = " + responseCode);
//		}

}
