
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class Main {

	public void foo(String name) {

	}

	public static void main(String[] args)
		throws Exception {
		Method method = Main.class.getDeclaredMethod("foo", String.class);
		System.out.println(method);

		for (Parameter param : method.getParameters()) {
			System.out.println(param + " > " + param.isNamePresent());
		}
	}
}
