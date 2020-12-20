package dataManagePackage;
import dataManagePackage.Receipt.Receipt;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Taxpayer {
	private String name;
	private String afm;
	private String familyStatus;
	private double income;
	private double basicTax;
	private double taxIncrease;
	private double taxDecrease;
	private double totalTax;
	private ArrayList<Receipt> receipts;
	
	public Taxpayer(String name, String afm, String familyStatus, String income){
		this.name = name;
		this.afm = afm;
		this.familyStatus = familyStatus;
		this.income = Double.parseDouble(income);
		setBasicTaxBasedOnFamilyStatus();
		taxIncrease = 0;
		taxDecrease = 0;
		
		receipts = new ArrayList<Receipt>();
	}
	
	private void setBasicTaxBasedOnFamilyStatus(){
		int[] upperBound = null;
		double[] base = null;
		double[] percentage = null;
		
		switch(familyStatus.toLowerCase()){
			case("married filing jointly"):
				upperBound = new int[]{36080, 90000, 143350, 254240};
				base = new double[]{1930.28, 5731.64, 9492.82, 18197.69};
				percentage = new double[]{7.05, 7.05, 7.85, 9.85};				
				break;
			case("married filing separately"):
				upperBound = new int[]{18040, 71680, 90000, 127120};
				base = new double[]{965.14, 4746.76, 6184.88, 9098.80};
				percentage = new double[]{7.05, 7.85, 7.85, 9.85};
				break;
			case("single"):
				upperBound = new int[]{24680, 81080, 90000, 152540};
				base = new double[]{1320.38, 5296.58, 5996.80, 10906.19};
				percentage = new double[]{7.05, 7.85, 7.85, 9.85};
				break;
			case("head of household"):
				upperBound = new int[]{30390, 90000, 122110, 203390};
				base = new double[]{1625.87, 5828.38, 8092.13, 14472.61};
				percentage = new double[]{7.05, 7.05, 7.85, 9.85};
				break;
		}
		
		basicTax = calculateTax(income, upperBound, base, percentage);
		totalTax = basicTax;
	}
	
	public double calculateTax(double totalIncome, int[] upperBound, double[] base, double[] percentage){
		double tax;
		
		if (totalIncome < upperBound[0]){
			tax = 0.0535 * totalIncome;
		}
		else if (totalIncome < upperBound[1]){
			tax = base[0] + ((percentage[0]/100) * (totalIncome-upperBound[0]));
		}
		else if (totalIncome < upperBound[2]){
			tax = base[1] + ((percentage[1]/100) * (totalIncome-upperBound[1]));
		}
		else if (totalIncome < upperBound[3]){
			tax = base[2] + ((percentage[2]/100) * (totalIncome-upperBound[2]));
		}
		else{
			tax = base[3] + ((percentage[3]/100) * (totalIncome-upperBound[3]));
		}
		
		return tax;
	}
	
	
	public String toString(){
		return "Name: "+name
				+"\nAFM: "+afm
				+"\nStatus: "+familyStatus
				+"\nIncome: "+String.format("%.2f", income)
				+"\nBasicTax: "+String.format("%.2f", basicTax)
				+"\nTaxIncrease: "+String.format("%.2f", taxIncrease)
				+"\nTaxDecrease: "+String.format("%.2f", taxDecrease);
	}
	
	public Receipt getReceipt(int receiptID){
		return receipts.get(receiptID);
	}
	
	public ArrayList<Receipt> getReceiptsArrayList(){
		return receipts;
	}
	
	public String[] getReceiptsList(){
		String[] receiptsList = new String[receipts.size()];
		
		int c = 0;
		for (Receipt receipt : receipts){
			receiptsList[c++] = receipt.getId() + " | " + receipt.getDate() + " | " + receipt.getAmount();
		}
		
		return receiptsList;
	}
	
	public double getReceiptsTotalAmount(String receiptType){
		double receiptsTotalAmount = 0;
		
		for (Receipt receipt : receipts){
			if (receipt.getKind().equals(receiptType)){
				receiptsTotalAmount += receipt.getAmount();
			}
		}
		
		return (new BigDecimal(receiptsTotalAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}
	
	public double getTotalReceiptsAmount(){
		double totalReceiptsAmount = 0;
		
		for (Receipt receipt : receipts){
			totalReceiptsAmount += receipt.getAmount();
		}
		
		return (new BigDecimal(totalReceiptsAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}
	
	public String getName(){
		return name;
	}
	
	public String getAFM(){
		return afm;
	}
	
	public String getFamilyStatus(){
		return familyStatus;
	}
	
	public double getIncome(){
		return (new BigDecimal(income).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}
	
	public double getBasicTax(){
		return (new BigDecimal(basicTax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}
	
	public double getTaxInxrease(){
		return (new BigDecimal(taxIncrease).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}
	
	public double getTaxDecrease(){
		return (new BigDecimal(taxDecrease).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}
	
	public double getTotalTax(){
		return (new BigDecimal(totalTax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}
	
	public void addReceiptToList(Receipt receipt){
		receipts.add(receipt);
		
		calculateTaxpayerTaxIncreaseOrDecreaseBasedOnReceipts();
	}
	
	public void removeReceiptFromList(int index){
		receipts.remove(index);
		
		calculateTaxpayerTaxIncreaseOrDecreaseBasedOnReceipts();
	}
	
	public void calculateTaxpayerTaxIncreaseOrDecreaseBasedOnReceipts(){
		double totalReceiptsAmount = 0;
		for (Receipt receipt : receipts){
			totalReceiptsAmount += receipt.getAmount();
		}
		
		taxIncrease = 0;
		taxDecrease = 0;
		if ((totalReceiptsAmount/(double)income) < 0.2){
			taxIncrease = basicTax * 0.08;
		}
		else if ((totalReceiptsAmount/(double)income) < 0.4){
			taxIncrease = basicTax * 0.04;
		}
		else if ((totalReceiptsAmount/(double)income) < 0.6){
			taxDecrease = basicTax * 0.15;
		}
		else{
			taxDecrease = basicTax * 0.30;
		}
		
		totalTax = basicTax + taxIncrease - taxDecrease;
	}
}