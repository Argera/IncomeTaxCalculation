package dataManagePackage.Receipt;

public class HealthReceipt extends Receipt{
	private final String kind = "Health";
	
	public HealthReceipt(String id, String date, String amount, String name, String country, String city, String street, String number){
		super(id, date, amount, name, country, city, street, number);
		super.kind = kind;
	}
}
