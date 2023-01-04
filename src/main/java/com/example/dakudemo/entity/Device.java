package com.example.dakudemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author yfgan
 * @create 2021-12-29 10:31
 */

public class Device {
    private Integer id;
    private String device_id;
    private String name;
    private String model;
    private Integer inventory_number;
    private Integer repair_number;
    private Integer lend_number;
    private Integer outwarehouse_number;
    private Integer scrap_number;
    private Integer category_id;
    private Integer fund_id;
    private String document_id;

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public Integer getFund_id() {
        return fund_id;
    }

    public void setFund_id(Integer fund_id) {
        this.fund_id = fund_id;
    }

    private String location;
    private String amount_unit;
    private String create_time;
    private String sale_company;
    private String product_company;
    private String unit_price;
    private String description;

    public Device(){

    }
    public Device(Integer id ,String device_id, String name, String model, Integer inventory_number, Integer repair_number, Integer lend_number, Integer outwarehouse_number, Integer scrap_number, Integer category_id,Integer fund_id, String location, String amount_unit, String create_time, String sale_company, String product_company, String unit_price, String description) {
        this.id = id;
        this.device_id = device_id;
        this.name = name;
        this.model = model;
        this.inventory_number = inventory_number;
        this.repair_number = repair_number;
        this.lend_number = lend_number;
        this.outwarehouse_number = outwarehouse_number;
        this.scrap_number = scrap_number;
        this.category_id = category_id;
        this.fund_id = fund_id;
        this.location = location;
        this.amount_unit = amount_unit;
        this.create_time = create_time;
        this.sale_company = sale_company;
        this.product_company = product_company;
        this.unit_price = unit_price;
        this.description = description;
    }
    public Device(String device_id, String name, String model, Integer inventory_number, Integer repair_number, Integer lend_number, Integer outwarehouse_number, Integer scrap_number, Integer category_id, Integer fund_id, String location, String amount_unit, String create_time, String sale_company, String product_company, String unit_price, String description) {
        this.device_id = device_id;
        this.name = name;
        this.model = model;
        this.inventory_number = inventory_number;
        this.repair_number = repair_number;
        this.lend_number = lend_number;
        this.outwarehouse_number = outwarehouse_number;
        this.scrap_number = scrap_number;
        this.category_id = category_id;
        this.fund_id = fund_id;
        this.location = location;
        this.amount_unit = amount_unit;
        this.create_time = create_time;
        this.sale_company = sale_company;
        this.product_company = product_company;
        this.unit_price = unit_price;
        this.description = description;
    }

    public Device(String device_id, String name, String model, Integer inventory_number, Integer repair_number, Integer lend_number, Integer outwarehouse_number, Integer scrap_number, Integer category_id, Integer fund_id, String document_id, String location, String amount_unit, String create_time, String sale_company, String product_company, String unit_price, String description) {
        this.device_id = device_id;
        this.name = name;
        this.model = model;
        this.inventory_number = inventory_number;
        this.repair_number = repair_number;
        this.lend_number = lend_number;
        this.outwarehouse_number = outwarehouse_number;
        this.scrap_number = scrap_number;
        this.category_id = category_id;
        this.fund_id = fund_id;
        this.document_id = document_id;
        this.location = location;
        this.amount_unit = amount_unit;
        this.create_time = create_time;
        this.sale_company = sale_company;
        this.product_company = product_company;
        this.unit_price = unit_price;
        this.description = description;
    }

    public Device(Integer id, String device_id, String name, String model, Integer inventory_number, Integer repair_number, Integer lend_number, Integer outwarehouse_number, Integer scrap_number, Integer category_id, Integer fund_id, String document_id, String location, String amount_unit, String create_time, String sale_company, String product_company, String unit_price, String description) {
        this.id = id;
        this.device_id = device_id;
        this.name = name;
        this.model = model;
        this.inventory_number = inventory_number;
        this.repair_number = repair_number;
        this.lend_number = lend_number;
        this.outwarehouse_number = outwarehouse_number;
        this.scrap_number = scrap_number;
        this.category_id = category_id;
        this.fund_id = fund_id;
        this.document_id = document_id;
        this.location = location;
        this.amount_unit = amount_unit;
        this.create_time = create_time;
        this.sale_company = sale_company;
        this.product_company = product_company;
        this.unit_price = unit_price;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getInventory_number() {
        return inventory_number;
    }

    public void setInventory_number(Integer inventory_number) {
        this.inventory_number = inventory_number;
    }

    public Integer getRepair_number() {
        return repair_number;
    }

    public void setRepair_number(Integer repair_number) {
        this.repair_number = repair_number;
    }

    public Integer getLend_number() {
        return lend_number;
    }

    public void setLend_number(Integer lend_number) {
        this.lend_number = lend_number;
    }

    public Integer getOutwarehouse_number() {
        return outwarehouse_number;
    }

    public void setOutwarehouse_number(Integer outwarehouse_number) {
        this.outwarehouse_number = outwarehouse_number;
    }

    public Integer getScrap_number() {
        return scrap_number;
    }

    public void setScrap_number(Integer scrap_number) {
        this.scrap_number = scrap_number;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAmount_unit() {
        return amount_unit;
    }

    public void setAmount_unit(String amount_unit) {
        this.amount_unit = amount_unit;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getSale_company() {
        return sale_company;
    }

    public void setSale_company(String sale_company) {
        this.sale_company = sale_company;
    }

    public String getProduct_company() {
        return product_company;
    }

    public void setProduct_company(String product_company) {
        this.product_company = product_company;
    }

    public String getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
