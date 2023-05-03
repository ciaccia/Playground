package test;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapTest {

	private final String KEY = "key";
	
	private final ConcurrentHashMap<String,String> map;
	private boolean computing;
	private boolean threadRunning;
	
	private ConcurrentHashMapTest() {
		map = new ConcurrentHashMap<>();
		computing = false;
		threadRunning = false;
	}

	private void test() {
		new Thread(() -> {
			System.out.println("Thread > Running");
			threadRunning = true;
			while (!computing) try { Thread.sleep(1000); } catch (InterruptedException e) {}
			System.out.println(String.format("Thread > Trying to insert a new value for %s", KEY));
			map.computeIfAbsent(KEY, (String key) -> { return "Y"; });
			System.out.println("Thread > Value inserted");
		}).start();
		
		// Wait for thread to start
		while (!threadRunning) try { Thread.sleep(100); } catch (InterruptedException e) {}
		
		map.computeIfAbsent(KEY, (String key) -> {
			System.out.println("ConcurrentMap > starging computing new value");
			computing = true;
			long start = System.currentTimeMillis();
			while ((System.currentTimeMillis() - start) < 10000) {
				try { Thread.sleep(100); } catch (InterruptedException e) {}
			}
			String result = "X";
			System.out.println(String.format("ConcurrentMap > Done computing new value. Returning %s", result));
			computing = false;
			return result;
		});
	}
	
	public static void main(String[] args) {
		new ConcurrentHashMapTest().test();
	}
}
