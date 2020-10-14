package com.hp.asi.hpic4vc.provider.data;

public class ProductHelpPageData implements Comparable <ProductHelpPageData> {

    private String title;
    private String url;
    private int order;
    private String helpUrl;
    private String web_root;
    
    public ProductHelpPageData() {
        this.title = null;
        this.url   = null;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public int getOrder () {
        return order;
    }

    public void setOrder (int order) {
        this.order = order;
    }

    public String getHelpUrl () {
        return helpUrl;
    }

    public void setHelpUrl (String helpUrl) {
        this.helpUrl = helpUrl;
    }

    public String getWeb_root () {
        return web_root;
    }

    public void setWeb_root (String web_root) {
        this.web_root = web_root;
    }

    @Override
    public String toString () {
        return "ProductHelpPageData [title=" + title + ", url=" + url
                + ", order=" + order + ", helpUrl=" + helpUrl + ", web_root="
                + web_root + "]";
    }

	@Override
	public int compareTo(ProductHelpPageData other) {
		if (this.order > other.order) {
			return 1;
		} else {
			return -1;
		}
	}
    
}
