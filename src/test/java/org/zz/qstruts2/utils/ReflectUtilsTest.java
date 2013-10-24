package org.zz.qstruts2.utils;

/**
 * @author xiangqh
 *
 */
public class ReflectUtilsTest {

	public static void main(String[] args) {

		A a = new A();
		System.out.println(ReflectUtils.invokeMethod(ReflectUtils.getMethod("ss", A.class), a, new Object[] {}));
		System.out.println(ReflectUtils.invokeMethod(ReflectUtils.getMethod("test", A.class), a, new Object[] {"123"}));
	}

	static class A {
		public String ss() {
			System.out.println("ss");
			return "ss";
		}

		public String test(String ss) {
			System.out.println(ss);
			return "hello " + ss;
		}

	}
}
