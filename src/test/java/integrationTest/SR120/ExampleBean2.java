package integrationTest.SR120;

import com.opencsv.bean.CsvBindByName;

public class ExampleBean2 {

	@CsvBindByName
	private String field1;

	@CsvBindByName(required = true)
	private String field2;

	@CsvBindByName
	private String field3;

}
