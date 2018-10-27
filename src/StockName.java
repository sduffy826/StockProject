
public class StockName {
	private String symbol;
	private String name;
	
	public String getName() {
		return name;
	}
	StockName(String _symbol, String _name) {
		this.setSymbol(_symbol);
		this.setName(_name);
	}
	private void setName(String name) {
		this.name = name;
	}
	public String getSymbol() {
		return symbol;
	}
	private void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String toString() {
		return this.getSymbol() + ": " + this.getName();
	}

}
