package hageldave.ezfftw;

import static org.junit.Assert.fail;

import java.util.function.Supplier;

public class JunitUtils {

	public static final double doubleTolerance = 1d/10000000;
	public static final float floatTolerance =   1f/10000;

	public static void testException(Runnable codeThatThrows, Class<? extends Throwable> exClass){
		boolean wasThrown = true;
		try{
			codeThatThrows.run();
			wasThrown = false;
		} catch(Throwable t){
			if(!exClass.isInstance(t)){
				fail(String.format("Expected Exception %s but got %s", exClass, t.getClass()));
			}
		}
		if(!wasThrown){
			fail(String.format("Expected Exception %s but none was thrown",exClass));
		}
	}

	public static void testWithMsg(Runnable test, Supplier<String> msg){
		try {
			test.run();
		} catch (AssertionError e) {
			throw new AssertionError(msg.get()+" : "+e.getMessage(), e);
		}
	}

}
