package tuple;

import io.vavr.Tuple;
import io.vavr.Tuple2;

public class Main {
    public static void main(String[] args) {
	    Tuple2<String, String> tuple = Tuple.of("hello", "world");

        System.out.println(tuple._1);
        System.out.println(tuple._2);

        System.out.println(tuple.map((a,b) -> Tuple.of(a + "-mapped", b + "-mapped")));

        System.out.println(tuple.map1(x -> x + "-mapped"));
        System.out.println(tuple.map2(x -> x + "-mapped"));

        System.out.println(tuple.apply((String a, String b) -> a + b));
    }
}
