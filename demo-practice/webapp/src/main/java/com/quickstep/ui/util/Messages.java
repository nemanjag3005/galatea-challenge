package com.quickstep.ui.util;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

public class Messages {
	public static class Common {
		public static final String ADDRESS = "Address";
		public static final String ADDRESS_DETAILS = "Address Details";
		public static final String ADDRESS_REQUIRED = "Address required.";
		public static final String APT_SUITE = "Apt./Suite";
		public static final String CANCEL = "Cancel";
		public static final String CITY = "City";
		public static final String CITY_REQUIRED = "City required.";
		public static final String CLEAR_SEARCH = "Clear search";
		public static final String COMMENTS = "Comments";
		public static final String CONFIRM = "Confirm";
		public static final String COUNTRY = "Country";
		public static final String COUNTRY_REQUIRED = "Country required.";
		public static final String FIRST_NAME = "First Name";
		public static final String FIRST_NAME_REQUIRED = "First Name required.";
		public static final String LAST_NAME = "Last Name";
		public static final String LAST_NAME_REQUIRED = "Last Name required.";
		public static final String NAME_REQUIRED = "Name required.";
		public static final String NOT_A_NUMBER = "Not a number";
		public static final String PHONE = "Phone";
		public static final String PHONE_FORMAT_VALIDATION = "Phone number not in correct format.";
		public static final String POSTAL_CODE_PO_BOX = "Postal Code/PO Box";
		public static final String POSTAL_CODE_PO_BOX_REQUIRED = "Postal Code/PO Box required.";
		public static final String PROVINCE_STATE = "Province/State";
		public static final String PROVINCE_STATE_REQUIRED = "Province/State required.";
		public static final String SAVE = "Save";
		public static final String SAVE_INFORMATION = "Save Information";
		public static final String SYSTEM_SENDER = "SYSTEM";
		public static final String SEARCH = "Search";
		public static final String SUBMIT = "Submit";
		public static final String UPLOAD = "Upload";
		public static final String SITE = "Site";
		public static final String DEFAULT_PARSE_ERROR_MESSAGE = "Could not parse value";
	}

	public static class Tank {
		public static final String NAME = "Tank Name";
		public static final String TYPE = "Fluid Type";
		public static final String CAPACITY = "Capacity";
		public static final String SITE_REQUIRED = "Site required.";
		public static final String TANK_NAME_REQUIRED = "Tank Name required.";
		public static final String TANK_FLUID_TYPE_REQUIRED = "Fluid Type required.";
		public static final String TANK_CAPACITY_REQUIRED = "Capacity required.";
		public static final String TANK_VOLUME_REQUIRED = "Volume required.";
	}

	public static class AddDriverDialogMessages {
		public static final String ADD_IMAGE = "Add Image";
		public static final String DRIVER_LICENSE = "Driver License";
		public static final String DRIVER_LICENSE_REQUIRED = "Driver license required.";
		public static final String DRIVER_LICENSE_EXPIRY = "Driver License Expiry";
		public static final String H2S_EXPIRY = "H2S Awareness Expiry Date";
		public static final String H2S_EXPIRY_REQUIRED = "H2S Awareness Expiry required.";
		public static final String SITE_ORIENTATION_EXPIRY = "Site Orientation Expiry Date";
		public static final String SITE_ORIENTATION_EXPIRY_REQUIRED = "Site Orientation Expiry required.";
		public static final String IMAGE = "Image";
		public static final String KRG_ID = "KRG ID";
		public static final String MOBILE_PHONE_NUMBER = "Mobile Phone Number";
		public static final String REMOVE_IMAGE = "Remove Image";
		public static final String COMMENTS = "Comments";
		public static final String COMMENTS_SIZE_EXCEEDED = "Comments can be maximum 512 characters long";
	}

	public static class AddTractorDialogMessages {
		public static final String EQUIPMENT_STATUS = "Equipment Status";
		public static final String LAST_INSPECTION = "Last Inspection";
		public static final String LAST_INSPECTION_COMMENT = "Last Inspection Comment";
		public static final String PLATE_NUMBER = "Plate Number";
		public static final String PLATE_NUMBER_REQUIRED = "Truck Plate Number required";
		public static final String TRUCK_MAKE = "Truck Make";
		public static final String TRUCK_MODEL = "Truck Model";
		public static final String TRUCK_YEAR = "Truck Year";
	}

	public static class AddTrailerDialogMessage {
		public static final String EQUIPMENT_STATUS = "Equipment Status";
		public static final String LAST_INSPECTION_COMMENT = "Last Inspection Comment";
		public static final String PLATE_NUMBER = "Plate Number";
		public static final String TRAILER_CAPACITY = "Trailer Capacity (L)";
	}

	public static class ChemicalInventoryViewMessages {
		public static final String ADD_NEW_ITEM = "Add New Item";
		public static final String CLOSING_INVENTORY = "Closing Inventory";
		public static final String CLOSING_INVENTORY_EXCEED = "Closing Inventory must not exceed current remain inventory";
		public static final String COST_PER_UNIT = "Cost Per Unit";
		public static final String COST_PER_UNIT_REQUIRED = "Cost per Unit required.";
		public static final String MAX_CAPACITY_REQUIRED = "Max Tank Capacity is required.";
		public static final String DAILY_TOTAL = "Daily Total (vol)";
		public static final String DAILY_TOTAL_COST = "Daily Total Cost";
		public static final String MAX_TANK_CAPACITY = "Max Capacity";
		public static final String DATE = "Date";
		public static final String DATE_REQUIRED = "Date requied.";
		public static final String ITEM = "Item";
		public static final String LOW_VOLUME_ALERT = "Low Volume Alert";
		public static final String NEW_PRODUCT = "New Product";
		public static final String PRODUCT = "Product";
		public static final String PRODUCT_EXIST = "Product already existed.";
		public static final String PRODUCT_NAME = "Product Name";
		public static final String PRODUCT_REQUIRED = "Product required.";
		public static final String PRODUCT_NAME_REQUIRED = "Product Name required.";
		public static final String RATE = "Rate (vol/day)";
		public static final String RECEIVED_BY = "Received by";
		public static final String SAVE_VOLUMES = "Save Volume";
		public static final String SHIPMENT_ID = "Shipment ID";
		public static final String SITE = "Site";
		public static final String UNIT = "Unit";
		public static final String UNIT_REQUIRED = "Unit type required.";
		public static final String SITE_REQUIRED = "Site required.";
		public static final String VOLUME = "Volume";
	}

	public static class CompanyEditViewMessage {
		public static final String ACCEPT_IMPORT_VARIANCE = "Accept Import Variance<br/>Enables auto approval of imported production values if within specific variance";
		public static final String AUTO_APPROVE_MANUAL_ENTRIES = "Auto Approve Manual Entries<br/>Enables auto approval of manual production entries made by company personnel";
		public static final String AUTO_LOGOUT = "Auto-Logout<br/>System will Auto-logout users after a predetermined period";
		public static final String COMPANY_NAME = "Company Name";
		public static final String HRS = "Hrs";
		public static final String VARIANCE_PERCENTAGE = "Variance (%)";
	}

	public static class DriverListViewMessages {
		public static final String FULL_NAME = "Full Name";
		public static final String KRG_ID = "KRG ID";
		public static final String LICENSE = "License";
		public static final String LICENSE_EXP = "License Expiry";
		public static final String NEW_DRIVER = "New Driver";
	}

	public static class EquipmentListViewMessages {
		public static final String ADD_TRACTOR = "Add Tractor";
		public static final String ADD_TRAILER = "Add Trailer";
		public static final String LAST_INSPECTION = "Last Inspection";
		public static final String STATUS = "Status";
		public static final String TRACTOR_PLATE_NUMBER = "Tractor Plate Number";
		public static final String TRAILER_PLATE_NUMBER = "Trailer Plate Number";
		public static final String TRAILER_CAPACITY = "Capacity";
		public static final String TRUCK_MAKE = "Truck Make";
		public static final String TRUCK_MODEL = "Truck Model";
		public static final String TRUCK_YEAR = "Truck Year";
    }

	public static class LoadViewCommonMessage {
		public static final String DRIVER_NAME = "Driver Name";
		public static final String TRACTOR_PLATE_NUMBER = "Tractor Plate Number";
		public static final String WEIGHT = "Weight (kg)";
	}

	public static class LoadDetailViewMessage {
		public static final String CRUDE_MANIFEST = "Crude Manifest";
		public static final String DRIVER_TRUCK_INFORMATION = "DRIVER & TRUCK INFORMATION";
		public static final String ENTER_LOAD_VOLUME = "Enter Load Volume";
		public static final String OFF_SPEC_LOAD = "Off Spec Load";
		public static final String ENTER_UNLOAD_VOLUME = "Enter Unload Volume";
		public static final String EDIT_UNLOAD_VOLUME = "Edit Unload Volume";
		public static final String HEADER = "Load: %s";
		public static final String INSPECTION_FORM = "Inspection Form";
		public static final String LOAD_DATE = "Load Date";
		public static final String LOAD_INFORMATION = "LOAD INFORMATION";
		public static final String LOAD_TIME = "Load Time";
		public static final String START_LOAD_TIME = "Start Load Time";
		public static final String FINISH_LOAD_TIME = "Finish Load Time";
		public static final String LOAD_WEIGHT = "Load Weight (kg)";
		public static final String LOAD_VOLUME = "Load Volume (bbl)";
		public static final String SECURITY_INSPECTION = "Security Inspection";
		public static final String SECURITY_INSPECTION_DETAILS = "Security Inspection Details";
		public static final String TITLE = "Load Info";
		public static final String TRUCK_INSPECTION = "Truck Inspection";
		public static final String TRUCK_INSPECTION_DETAILS = "Truck Inspection Details";
		public static final String UNLOAD_DATE = "Unload Date";
		public static final String UNLOAD_INFORMATION = "UNLOAD INFORMATION";
		public static final String UNLOAD_TIME = "Unload Time";
		public static final String SALES_OIL_PROPERTIES = "SALES OIL PROPERTIES";
		public static final String SALES_OIL_SAMPLE_DATE = "Sample Date";
		public static final String SALES_OIL_SAMPLE_TIME = "Sample Time";
		public static final String SALES_OIL_BSW = "BS&W";
		public static final String SALES_OIL_SALT_CONTENT = "Salt Content";
		public static final String SALES_OIL_RVP = "RVP";
		public static final String SALES_OIL_H2S = "H2S Content";
		public static final String SALES_OIL_DENSITY = "Density";
		public static final String SALES_OIL_API = "API";
		public static final String SALES_OIL_TEMPERATURE = "Temperature";
		public static final String SALES_OIL_ENTERED_BY = "Entered By";
		public static final String OIL_LOAD = "OIL LOAD";
		public static final String PRODUCED_WATER_LOAD = "PRODUCED WATER LOAD";
		public static final String PREFIX = "LDV";
		public static final String TRUCK_TICKET = "Truck Ticket";
	}

	public static class AttachmentThumbMessage {
		public static final String PREFIX = "AT";
		public static final String JPEG = "image/jpeg";
		public static final String PNG = "image/png";
		public static final String GIF = "image/gif";
		public static final String PDF = "application/pdf";
		public static final int TWO_MB = 2 * 1024 * 1024;
	}

	public static class LoadsListViewMessage {
		public static final String MANIFEST_NUMBER = "Manifest Number";
		public static final String LOAD_NUMBER = "Load #";
		public static final String LOAD_STATUS = "Load Status";
		public static final String OIL_LOAD = "Oil Load";
		public static final String PRODUCED_WATER_LOAD = "Produced Water Load";
		public static final String TIME_IN = "Time In";
		public static final String TIME_OUT = "Time Out";
		public static final String LOAD_TYPE = "Load Type";
		public static final String UNLOADING_DATE = "Unloading Date";
		public static final String VOLUME = "Vol (bbl)";
	}

	public static class SafetyInspectionDialog {
		public static final String COMPLETE_INSPECTION = "Complete Inspection";
		public static final String NEXT_INSPECTION = "Next Tab";
		public static final String PREV_INSPECTION = "Prev Tab";
		public static final String INSPECTION_QUESTION_REQUIRED = "Question must be answered.";
		public static final String DID_TRACTOR_PASS_INSPECTION = "Did the Tractor PASS the Inspection";
		public static final String DID_DRIVER_PASS_INSPECTION = "Does the driver have all required PPE? (Hard Hat, Safety Glasses," +
				" Gloves, Hearing Protection, Flame Resistant Coveralls with approved reflective markings Certified steel toe boots)";
		public static final String DID_TRAILER_PASS_INSPECTION = "Did the Trailer PASS the Inspection";
	}

	public static class SelectDriverEquipmentDialogMessages {
		public static final String ADD_NEW_DRIVER = "Add New Driver";
		public static final String ADD_NEW_TRACTOR = "Add New Tractor";
		public static final String ADD_NEW_TRAILER = "Add New Trailer";
		public static final String ADD_EQUIPMENT = "Add Equipment";
		public static final String DRIVER_ALREADY_ASSIGNED = "Selected driver is already assigned.";
		public static final String DRIVER_EXISTED = "Driver with this License Number is already existed";
		public static final String DRIVER_NAME = "Driver Name";
		public static final String DRIVER_LICENSE_NUMBER = "Driver's License Number";
		public static final String FACILITY = "Facility";
		public static final String TRACTOR_ALREADY_ASSIGNED = "Selected tractor is already assigned.";
		public static final String TRACTOR_PLATE_NO = "Tractor Plate No.";
		public static final String TRAILER_ALREADY_ASSIGNED = "Selected trailer is already assigned.";
		public static final String TRACTOR_EXISTED = "Tractor with this Plate Number is already existed";
		public static final String TRAILER_PLATE_NO = "Trailer Plate No.";
		public static final String TRAILER_EXISTED = "Trailer with this Plate Number is already existed";

	}

	public static class TruckScheduleViewMessage {
		public static final String ADD_DRIVER = "Add Driver";
		public static final String DRIVER = "Driver: %s";
		public static final String PLATE = "Plate: %s";
		public static final String STATUS = "Status: %s";
		public static final String TIME_IN = "Time In: %s";
		public static final String DROP_NOT_ALLOW = "Please complete this truck's inspection prior to assigning to a load site.";
	}

	public static class TankInventoryViewMessage {
		public static final String ADD_TANK = "Add a Tank";
		public static final String ADD_VOLUME = "Record Volume";
	}

	public static class UnloadVolumeDialogMessages {
		public static final String CAR_SEAL = "Car Seal's";
		public static final String CAR_SEAL_REQUIRED = "Car Seal's required.";
		public static final String CAR_SEAL_RECEIVED = "Car Seal's received";

		public static final String TRUCK_WEIGHT_LOADED = "Truck Weight Loaded";
		public static final String TRUCK_WEIGHT_LOADED_REQUIRED = "Truck Weight Loaded required.";
		public static final String TRUCK_WEIGHT_UNLOADED = "Truck Weight Unloaded";
		public static final String TRUCK_WEIGHT_UNLOADED_REQUIRED = "Truck Weight Unloaded required.";

		public static final String UNLOAD_TIME = "Unload Time";
		public static final String UNLOAD_INFORMATION = "UNLOAD INFORMATION";

		public static final String UNLOAD_TICKET = "Unload Ticket ID";
		public static final String UNLOAD_TICKET_REQUIRED = "Unload Ticket ID required.";

		public static final String UNLOAD_TIME_IN = "Unload Time In";
		public static final String UNLOAD_TIME_IN_REQUIRED = "Unload Time In required.";
		public static final String UNLOAD_TIME_OUT = "Unload Time Out";
		public static final String UNLOAD_TIME_OUT_REQUIRED = "Unload Time Out required.";
		public static final String PRODUCT_NET_WEIGHT_PRODUCT = "Net Weight of Product (kg)";
		public static final String PRODUCT_NET_WEIGHT_PRODUCT_REQUIRED = "Net Weight of Product (kg) required.";
		public static final String BSW = "BS&W";
		public static final String BSW_REQUIRED = "BS&W required.";
		public static final String PRODUCT_TEMPERATURE = "Product Temperature";
		public static final String PRODUCT_TEMPERATURE_REQUIRED = "Product Temperature required.";
		public static final String PRODUCT_API = "Product API";
		public static final String PRODUCT_API_REQUIRED = "Product API required.";
		public static final String PRODUCT_SULPHUR = "Product Sulphur";
		public static final String PRODUCT_SULPHUR_REQUIRED = "Product Sulphur required.";
		public static final String PRODUCT_WATER = "Product Water";
		public static final String PRODUCT_WATER_REQUIRED = "Product Water required.";
		public static final String PRODUCT_SPEC_GRAVITY = "Product Specific Gravity";
		public static final String PRODUCT_SPEC_GRAVITY_REQUIRED = "Product Specific Gravity required.";
		public static final String PRODUCT_NET_VOLUME = "Net Volume of Product (bbl)";
		public static final String PRODUCT_NET_VOLUME_REQUIRED = "Net Volume of Product (bbl) required.";
	}
}

