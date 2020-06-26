package com.increasingly.importbulkdata.util;

public class Constants
{
	public static final Integer FILE_COMPRESSION_TYPE_NONE = 0;
	public static final Integer FILE_COMPRESSION_TYPE_GZIP = 1;
	public static final Integer FILE_COMPRESSION_TYPE_ZIP = 2;
	public static final Integer OPERATION_TYPE_INSERT = 1;
	public static final Integer OPERATION_TYPE_UPDATE = 2;
	
	public static final Integer CURRENCY_FORMAT_DOT = 1;
	public static final Integer CURRENCY_FORMAT_COMMA = 2;

	
	public static final String FILE_EXTENSION_EXE = "exe";
	
	public static final String CONTENT_TYPE_JSON = "json";
	public static final String CONTENT_TYPE_XML = "xml";
	public static final String CONTENT_TYPE_CSV = "csv";
	public static final String CONTENT_TYPE_TXT = "txt";
	
	public static final String REQUEST_TYPE_API = "api";
	public static final String REQUEST_TYPE_FILE = "file";

	public static final String FILE_EXTENSION_ZIP = "zip";
	public static final String FILE_EXTENSION_GZ = "gz";
	public static final String FILE_EXTENSION_DEFLATE = "deflate";

	public static final String PROTOCOL_FTP = "ftp";
	public static final String PROTOCOL_HTTP = "http";
	public static final String PROTOCOL_HTTPS = "https";
	
	public static final String FEED_URL = "feed_url";
	public static final String DELIMITER = "feed_delimiter";
	public static final String CATEGORY_DELIMITER = "category_delimiter";
	public static final String CHARACTER_SET_ENCODER = "character_set_encoder";
	public static final String IS_LARGE_FEED = "is_large_feed";	
	public static final String IS_API = "is_api";
	public static final String API_QUERY_LIMIT = "api_query_limit";
	public static final String IS_MAGENTO_PRODUCT_EXPORT = "is_magento_product_export";	
	public static final String CUSTOMIZATION_TYPE = "customization_type";
	public static final String MAGENTO_VERSION = "magento_version";
	
	// Product details
	public static final String PRODUCT_ID = "product_id";
	public static final String PRODUCT_NAME = "product_name";
	public static final String PRODUCT_SKU = "product_sku";
	public static final String PRODUCT_PRICE = "price";
		
	public static final String SPECIAL_PRICE = "special_price";
	public static final String IMAGE_URL = "image_url";
	public static final String PRODUCT_URL = "product_url";
	
	public static final String DESCRIPTION = "description";
	public static final String SHORT_DESCRIPTION = "short_description";
	public static final String CLIENT_PRODUCT_STATUS = "client_product_status";
	public static final String PRODUCT_TYPE = "product_type";
	public static final String MANUFACTURER = "manufacturer";
	
	public static final String QUANTITY = "quantity";
	public static final String CREATED_DATE = "created_date_at_source";
	public static final String UPDATED_DATE = "updated_date_at_source";	
	public static final String CATEGORIES = "categories";
	public static final String ASSOCIATED_PRODUCTS = "associated_products";	
	public static final String RELATED_PRODUCTS = "related_products";
	public static final String UP_SELL_PRODUCTS = "up_sell_products";
	public static final String CROSS_SELL_PRODUCTS = "cross_sell_products";
	public static final String PRODUCT_INVENTORY_DATA = "inventory_details";
	public static final String PRODUCT_OPTIONS = "product_options";
	public static final String ATTRIBUTE_DETAILS = "attribute_details";
	
	public static final String COLOR = "color";
	public static final String SIZE = "size";
	public static final String WEIGHT = "weight";
	public static final String VISIBILITY = "visibility";
	
	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String FEED_ID = "feed_id";
	public static final String CLIENT_ID = "client_id";
	public static final String PLATFORM_ID = "platform_id";
	public static final String TOTAL_PRODUCT_COUNT = "total_product_count";
	public static final String TOTAL_ORDER_COUNT = "total_order_count";
	public static final String CATEGORY_LIST = "category_list";
	public static final String PRODUCT_LIST = "product_list";
	public static final String ORDER_LIST = "order_list";
	public static final String ORDER_ITEM_LIST = "order_item_list";
	public static final String ASSOCIATED_PRODUCT_LIST = "associated_product_list";
	public static final String OTHER_IMAGE_LIST = "other_image_list";
	public static final String PRODUCT_INVENTORY_LIST = "product_inventory_data";
	public static final String PRODUCT_OPTIONS_LIST = "product_options_data";
	
	public static final String FIELD1 = "field1";	
	public static final String FIELD2 = "field2";
	public static final String FIELD3 = "field3";
	public static final String FIELD4 = "field4";
	public static final String FIELD5 = "field5";
	public static final String FIELD6 = "field6";
		
	public static final String CHILD_PRODUCT_ID = "child_product_id";
	public static final String CHILD_PRODUCT_SKU = "child_product_sku";
	public static final String STORE_ID = "store_id";
	public static final String ATTRIBUTE_CODE = "attribute_code";
	public static final String ATTRIBUTE_ID = "attribute_id";
	public static final String ATTRIBUTE_LABEL = "attribute_label";
	public static final String OPTION_ID = "option_id";
	public static final String OPTION_TEXT = "option_text";
	public static final String OPTION_IMAGE_URL = "option_image_url";
	public static final String IS_PERCENT = "is_percent";
	public static final String PRICING_VALUE = "pricing_value";
	public static final String COLOR_CODE = "color_code";
	public static final String FIELD_TYPE = "field_type";
		
	public static final String IS_FIRST_SET_RECORD = "isFirstSetRecord";
	public static final String IS_LAST_SET_RECORD = "isLastSetRecord";
	public static final String STORE_CODE = "store_code";

	public static final String CLIENT_ORDER_ID = "client_order_id";
	
	public static final String ORDER_STATUS = "order_status";
	public static final String ORDER_AMOUNT = "order_amount";
	public static final String ORDER_TIME = "order_time";
	public static final String COUPON_CODE = "coupon_code";
	public static final String DISCOUNT_AMOUNT = "discount_amount";
	public static final String TAX_AMOUNT = "tax_amount";
	public static final String SHIPPING_AMOUNT = "shipping_amount";
	public static final String SHIPPING_METHOD = "shipping_method";
	public static final String CURRENCY_CODE = "currency_code";
	public static final String PAYMENT_METHOD = "payment_method";
	public static final String USER_IP = "user_ip";
	public static final String USER_AGENT = "user_agent";
	public static final String CUSTOMER_EMAIL = "customer_email";
	public static final String CUSTOMER_NAME = "customer_name";
	public static final String CUSTOMER_FIRST_NAME = "first_name";	
	public static final String CUSTOMER_LAST_NAME = "last_name";
	public static final String VISITOR_ID = "visitor_id";
	public static final String ITEMS = "items";
	public static final String ATTRIBUTE_QUANTITY = "attribute_quantity";
	public static final String ATTRIBUTE_PRICE = "attribute_price";
	public static final String ATTRIBUTE_SPECIAL_PRICE = "attribute_special_price";
	
	public static final String LOG_INFO = "[INFO]";
	public static final String LOG_ERROR = "[ERROR]";
	public static final String LOG_APPLICATION_FLOW = "[FLOW]";
	
	public static final String IS_DIFFERENTIAL_FEED = "is_differential_feed";
	public static final String UPDATED_FEED_FILE_NAME = "file_name";

}