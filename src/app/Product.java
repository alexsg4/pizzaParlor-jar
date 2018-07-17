package app;

public abstract class Product extends PricedItem {

    @Override
    public final String getTable() {
        return "Products";
    }

    private int type = ID_UNUSED;

    protected void setType(int type){
        if(type > ID_UNUSED)
        {
            this.type = type;
        }
    }

    protected Product()
    {
        super();
    }

    protected Product(int type)
    {
        super();
        setType(type);
    }

    protected Product(String name, double unitPrice){
        super(name, unitPrice);
    }

    protected Product(String name){
       super(name);
    }

    public int getType(){ return type; }

    public abstract String getTypeName();

}
