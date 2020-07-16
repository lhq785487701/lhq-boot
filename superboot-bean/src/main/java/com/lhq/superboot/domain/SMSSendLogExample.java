package com.lhq.superboot.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SMSSendLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SMSSendLogExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andSmsSendLogIdIsNull() {
            addCriterion("sms_send_log_id is null");
            return (Criteria) this;
        }

        public Criteria andSmsSendLogIdIsNotNull() {
            addCriterion("sms_send_log_id is not null");
            return (Criteria) this;
        }

        public Criteria andSmsSendLogIdEqualTo(Integer value) {
            addCriterion("sms_send_log_id =", value, "smsSendLogId");
            return (Criteria) this;
        }

        public Criteria andSmsSendLogIdNotEqualTo(Integer value) {
            addCriterion("sms_send_log_id <>", value, "smsSendLogId");
            return (Criteria) this;
        }

        public Criteria andSmsSendLogIdGreaterThan(Integer value) {
            addCriterion("sms_send_log_id >", value, "smsSendLogId");
            return (Criteria) this;
        }

        public Criteria andSmsSendLogIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("sms_send_log_id >=", value, "smsSendLogId");
            return (Criteria) this;
        }

        public Criteria andSmsSendLogIdLessThan(Integer value) {
            addCriterion("sms_send_log_id <", value, "smsSendLogId");
            return (Criteria) this;
        }

        public Criteria andSmsSendLogIdLessThanOrEqualTo(Integer value) {
            addCriterion("sms_send_log_id <=", value, "smsSendLogId");
            return (Criteria) this;
        }

        public Criteria andSmsSendLogIdIn(List<Integer> values) {
            addCriterion("sms_send_log_id in", values, "smsSendLogId");
            return (Criteria) this;
        }

        public Criteria andSmsSendLogIdNotIn(List<Integer> values) {
            addCriterion("sms_send_log_id not in", values, "smsSendLogId");
            return (Criteria) this;
        }

        public Criteria andSmsSendLogIdBetween(Integer value1, Integer value2) {
            addCriterion("sms_send_log_id between", value1, value2, "smsSendLogId");
            return (Criteria) this;
        }

        public Criteria andSmsSendLogIdNotBetween(Integer value1, Integer value2) {
            addCriterion("sms_send_log_id not between", value1, value2, "smsSendLogId");
            return (Criteria) this;
        }

        public Criteria andSendPhoneIsNull() {
            addCriterion("send_phone is null");
            return (Criteria) this;
        }

        public Criteria andSendPhoneIsNotNull() {
            addCriterion("send_phone is not null");
            return (Criteria) this;
        }

        public Criteria andSendPhoneEqualTo(String value) {
            addCriterion("send_phone =", value, "sendPhone");
            return (Criteria) this;
        }

        public Criteria andSendPhoneNotEqualTo(String value) {
            addCriterion("send_phone <>", value, "sendPhone");
            return (Criteria) this;
        }

        public Criteria andSendPhoneGreaterThan(String value) {
            addCriterion("send_phone >", value, "sendPhone");
            return (Criteria) this;
        }

        public Criteria andSendPhoneGreaterThanOrEqualTo(String value) {
            addCriterion("send_phone >=", value, "sendPhone");
            return (Criteria) this;
        }

        public Criteria andSendPhoneLessThan(String value) {
            addCriterion("send_phone <", value, "sendPhone");
            return (Criteria) this;
        }

        public Criteria andSendPhoneLessThanOrEqualTo(String value) {
            addCriterion("send_phone <=", value, "sendPhone");
            return (Criteria) this;
        }

        public Criteria andSendPhoneLike(String value) {
            addCriterion("send_phone like", value, "sendPhone");
            return (Criteria) this;
        }

        public Criteria andSendPhoneNotLike(String value) {
            addCriterion("send_phone not like", value, "sendPhone");
            return (Criteria) this;
        }

        public Criteria andSendPhoneIn(List<String> values) {
            addCriterion("send_phone in", values, "sendPhone");
            return (Criteria) this;
        }

        public Criteria andSendPhoneNotIn(List<String> values) {
            addCriterion("send_phone not in", values, "sendPhone");
            return (Criteria) this;
        }

        public Criteria andSendPhoneBetween(String value1, String value2) {
            addCriterion("send_phone between", value1, value2, "sendPhone");
            return (Criteria) this;
        }

        public Criteria andSendPhoneNotBetween(String value1, String value2) {
            addCriterion("send_phone not between", value1, value2, "sendPhone");
            return (Criteria) this;
        }

        public Criteria andSendApiIsNull() {
            addCriterion("send_api is null");
            return (Criteria) this;
        }

        public Criteria andSendApiIsNotNull() {
            addCriterion("send_api is not null");
            return (Criteria) this;
        }

        public Criteria andSendApiEqualTo(String value) {
            addCriterion("send_api =", value, "sendApi");
            return (Criteria) this;
        }

        public Criteria andSendApiNotEqualTo(String value) {
            addCriterion("send_api <>", value, "sendApi");
            return (Criteria) this;
        }

        public Criteria andSendApiGreaterThan(String value) {
            addCriterion("send_api >", value, "sendApi");
            return (Criteria) this;
        }

        public Criteria andSendApiGreaterThanOrEqualTo(String value) {
            addCriterion("send_api >=", value, "sendApi");
            return (Criteria) this;
        }

        public Criteria andSendApiLessThan(String value) {
            addCriterion("send_api <", value, "sendApi");
            return (Criteria) this;
        }

        public Criteria andSendApiLessThanOrEqualTo(String value) {
            addCriterion("send_api <=", value, "sendApi");
            return (Criteria) this;
        }

        public Criteria andSendApiLike(String value) {
            addCriterion("send_api like", value, "sendApi");
            return (Criteria) this;
        }

        public Criteria andSendApiNotLike(String value) {
            addCriterion("send_api not like", value, "sendApi");
            return (Criteria) this;
        }

        public Criteria andSendApiIn(List<String> values) {
            addCriterion("send_api in", values, "sendApi");
            return (Criteria) this;
        }

        public Criteria andSendApiNotIn(List<String> values) {
            addCriterion("send_api not in", values, "sendApi");
            return (Criteria) this;
        }

        public Criteria andSendApiBetween(String value1, String value2) {
            addCriterion("send_api between", value1, value2, "sendApi");
            return (Criteria) this;
        }

        public Criteria andSendApiNotBetween(String value1, String value2) {
            addCriterion("send_api not between", value1, value2, "sendApi");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeIsNull() {
            addCriterion("template_type is null");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeIsNotNull() {
            addCriterion("template_type is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeEqualTo(String value) {
            addCriterion("template_type =", value, "templateType");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeNotEqualTo(String value) {
            addCriterion("template_type <>", value, "templateType");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeGreaterThan(String value) {
            addCriterion("template_type >", value, "templateType");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeGreaterThanOrEqualTo(String value) {
            addCriterion("template_type >=", value, "templateType");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeLessThan(String value) {
            addCriterion("template_type <", value, "templateType");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeLessThanOrEqualTo(String value) {
            addCriterion("template_type <=", value, "templateType");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeLike(String value) {
            addCriterion("template_type like", value, "templateType");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeNotLike(String value) {
            addCriterion("template_type not like", value, "templateType");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeIn(List<String> values) {
            addCriterion("template_type in", values, "templateType");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeNotIn(List<String> values) {
            addCriterion("template_type not in", values, "templateType");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeBetween(String value1, String value2) {
            addCriterion("template_type between", value1, value2, "templateType");
            return (Criteria) this;
        }

        public Criteria andTemplateTypeNotBetween(String value1, String value2) {
            addCriterion("template_type not between", value1, value2, "templateType");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeIsNull() {
            addCriterion("template_code is null");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeIsNotNull() {
            addCriterion("template_code is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeEqualTo(String value) {
            addCriterion("template_code =", value, "templateCode");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeNotEqualTo(String value) {
            addCriterion("template_code <>", value, "templateCode");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeGreaterThan(String value) {
            addCriterion("template_code >", value, "templateCode");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeGreaterThanOrEqualTo(String value) {
            addCriterion("template_code >=", value, "templateCode");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeLessThan(String value) {
            addCriterion("template_code <", value, "templateCode");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeLessThanOrEqualTo(String value) {
            addCriterion("template_code <=", value, "templateCode");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeLike(String value) {
            addCriterion("template_code like", value, "templateCode");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeNotLike(String value) {
            addCriterion("template_code not like", value, "templateCode");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeIn(List<String> values) {
            addCriterion("template_code in", values, "templateCode");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeNotIn(List<String> values) {
            addCriterion("template_code not in", values, "templateCode");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeBetween(String value1, String value2) {
            addCriterion("template_code between", value1, value2, "templateCode");
            return (Criteria) this;
        }

        public Criteria andTemplateCodeNotBetween(String value1, String value2) {
            addCriterion("template_code not between", value1, value2, "templateCode");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusDetailIsNull() {
            addCriterion("status_detail is null");
            return (Criteria) this;
        }

        public Criteria andStatusDetailIsNotNull() {
            addCriterion("status_detail is not null");
            return (Criteria) this;
        }

        public Criteria andStatusDetailEqualTo(String value) {
            addCriterion("status_detail =", value, "statusDetail");
            return (Criteria) this;
        }

        public Criteria andStatusDetailNotEqualTo(String value) {
            addCriterion("status_detail <>", value, "statusDetail");
            return (Criteria) this;
        }

        public Criteria andStatusDetailGreaterThan(String value) {
            addCriterion("status_detail >", value, "statusDetail");
            return (Criteria) this;
        }

        public Criteria andStatusDetailGreaterThanOrEqualTo(String value) {
            addCriterion("status_detail >=", value, "statusDetail");
            return (Criteria) this;
        }

        public Criteria andStatusDetailLessThan(String value) {
            addCriterion("status_detail <", value, "statusDetail");
            return (Criteria) this;
        }

        public Criteria andStatusDetailLessThanOrEqualTo(String value) {
            addCriterion("status_detail <=", value, "statusDetail");
            return (Criteria) this;
        }

        public Criteria andStatusDetailLike(String value) {
            addCriterion("status_detail like", value, "statusDetail");
            return (Criteria) this;
        }

        public Criteria andStatusDetailNotLike(String value) {
            addCriterion("status_detail not like", value, "statusDetail");
            return (Criteria) this;
        }

        public Criteria andStatusDetailIn(List<String> values) {
            addCriterion("status_detail in", values, "statusDetail");
            return (Criteria) this;
        }

        public Criteria andStatusDetailNotIn(List<String> values) {
            addCriterion("status_detail not in", values, "statusDetail");
            return (Criteria) this;
        }

        public Criteria andStatusDetailBetween(String value1, String value2) {
            addCriterion("status_detail between", value1, value2, "statusDetail");
            return (Criteria) this;
        }

        public Criteria andStatusDetailNotBetween(String value1, String value2) {
            addCriterion("status_detail not between", value1, value2, "statusDetail");
            return (Criteria) this;
        }

        public Criteria andRequestCodeIsNull() {
            addCriterion("request_code is null");
            return (Criteria) this;
        }

        public Criteria andRequestCodeIsNotNull() {
            addCriterion("request_code is not null");
            return (Criteria) this;
        }

        public Criteria andRequestCodeEqualTo(String value) {
            addCriterion("request_code =", value, "requestCode");
            return (Criteria) this;
        }

        public Criteria andRequestCodeNotEqualTo(String value) {
            addCriterion("request_code <>", value, "requestCode");
            return (Criteria) this;
        }

        public Criteria andRequestCodeGreaterThan(String value) {
            addCriterion("request_code >", value, "requestCode");
            return (Criteria) this;
        }

        public Criteria andRequestCodeGreaterThanOrEqualTo(String value) {
            addCriterion("request_code >=", value, "requestCode");
            return (Criteria) this;
        }

        public Criteria andRequestCodeLessThan(String value) {
            addCriterion("request_code <", value, "requestCode");
            return (Criteria) this;
        }

        public Criteria andRequestCodeLessThanOrEqualTo(String value) {
            addCriterion("request_code <=", value, "requestCode");
            return (Criteria) this;
        }

        public Criteria andRequestCodeLike(String value) {
            addCriterion("request_code like", value, "requestCode");
            return (Criteria) this;
        }

        public Criteria andRequestCodeNotLike(String value) {
            addCriterion("request_code not like", value, "requestCode");
            return (Criteria) this;
        }

        public Criteria andRequestCodeIn(List<String> values) {
            addCriterion("request_code in", values, "requestCode");
            return (Criteria) this;
        }

        public Criteria andRequestCodeNotIn(List<String> values) {
            addCriterion("request_code not in", values, "requestCode");
            return (Criteria) this;
        }

        public Criteria andRequestCodeBetween(String value1, String value2) {
            addCriterion("request_code between", value1, value2, "requestCode");
            return (Criteria) this;
        }

        public Criteria andRequestCodeNotBetween(String value1, String value2) {
            addCriterion("request_code not between", value1, value2, "requestCode");
            return (Criteria) this;
        }

        public Criteria andRequestMessageIsNull() {
            addCriterion("request_message is null");
            return (Criteria) this;
        }

        public Criteria andRequestMessageIsNotNull() {
            addCriterion("request_message is not null");
            return (Criteria) this;
        }

        public Criteria andRequestMessageEqualTo(String value) {
            addCriterion("request_message =", value, "requestMessage");
            return (Criteria) this;
        }

        public Criteria andRequestMessageNotEqualTo(String value) {
            addCriterion("request_message <>", value, "requestMessage");
            return (Criteria) this;
        }

        public Criteria andRequestMessageGreaterThan(String value) {
            addCriterion("request_message >", value, "requestMessage");
            return (Criteria) this;
        }

        public Criteria andRequestMessageGreaterThanOrEqualTo(String value) {
            addCriterion("request_message >=", value, "requestMessage");
            return (Criteria) this;
        }

        public Criteria andRequestMessageLessThan(String value) {
            addCriterion("request_message <", value, "requestMessage");
            return (Criteria) this;
        }

        public Criteria andRequestMessageLessThanOrEqualTo(String value) {
            addCriterion("request_message <=", value, "requestMessage");
            return (Criteria) this;
        }

        public Criteria andRequestMessageLike(String value) {
            addCriterion("request_message like", value, "requestMessage");
            return (Criteria) this;
        }

        public Criteria andRequestMessageNotLike(String value) {
            addCriterion("request_message not like", value, "requestMessage");
            return (Criteria) this;
        }

        public Criteria andRequestMessageIn(List<String> values) {
            addCriterion("request_message in", values, "requestMessage");
            return (Criteria) this;
        }

        public Criteria andRequestMessageNotIn(List<String> values) {
            addCriterion("request_message not in", values, "requestMessage");
            return (Criteria) this;
        }

        public Criteria andRequestMessageBetween(String value1, String value2) {
            addCriterion("request_message between", value1, value2, "requestMessage");
            return (Criteria) this;
        }

        public Criteria andRequestMessageNotBetween(String value1, String value2) {
            addCriterion("request_message not between", value1, value2, "requestMessage");
            return (Criteria) this;
        }

        public Criteria andSendTimeIsNull() {
            addCriterion("send_time is null");
            return (Criteria) this;
        }

        public Criteria andSendTimeIsNotNull() {
            addCriterion("send_time is not null");
            return (Criteria) this;
        }

        public Criteria andSendTimeEqualTo(Date value) {
            addCriterion("send_time =", value, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeNotEqualTo(Date value) {
            addCriterion("send_time <>", value, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeGreaterThan(Date value) {
            addCriterion("send_time >", value, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("send_time >=", value, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeLessThan(Date value) {
            addCriterion("send_time <", value, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeLessThanOrEqualTo(Date value) {
            addCriterion("send_time <=", value, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeIn(List<Date> values) {
            addCriterion("send_time in", values, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeNotIn(List<Date> values) {
            addCriterion("send_time not in", values, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeBetween(Date value1, Date value2) {
            addCriterion("send_time between", value1, value2, "sendTime");
            return (Criteria) this;
        }

        public Criteria andSendTimeNotBetween(Date value1, Date value2) {
            addCriterion("send_time not between", value1, value2, "sendTime");
            return (Criteria) this;
        }

        public Criteria andBizIdIsNull() {
            addCriterion("biz_id is null");
            return (Criteria) this;
        }

        public Criteria andBizIdIsNotNull() {
            addCriterion("biz_id is not null");
            return (Criteria) this;
        }

        public Criteria andBizIdEqualTo(String value) {
            addCriterion("biz_id =", value, "bizId");
            return (Criteria) this;
        }

        public Criteria andBizIdNotEqualTo(String value) {
            addCriterion("biz_id <>", value, "bizId");
            return (Criteria) this;
        }

        public Criteria andBizIdGreaterThan(String value) {
            addCriterion("biz_id >", value, "bizId");
            return (Criteria) this;
        }

        public Criteria andBizIdGreaterThanOrEqualTo(String value) {
            addCriterion("biz_id >=", value, "bizId");
            return (Criteria) this;
        }

        public Criteria andBizIdLessThan(String value) {
            addCriterion("biz_id <", value, "bizId");
            return (Criteria) this;
        }

        public Criteria andBizIdLessThanOrEqualTo(String value) {
            addCriterion("biz_id <=", value, "bizId");
            return (Criteria) this;
        }

        public Criteria andBizIdLike(String value) {
            addCriterion("biz_id like", value, "bizId");
            return (Criteria) this;
        }

        public Criteria andBizIdNotLike(String value) {
            addCriterion("biz_id not like", value, "bizId");
            return (Criteria) this;
        }

        public Criteria andBizIdIn(List<String> values) {
            addCriterion("biz_id in", values, "bizId");
            return (Criteria) this;
        }

        public Criteria andBizIdNotIn(List<String> values) {
            addCriterion("biz_id not in", values, "bizId");
            return (Criteria) this;
        }

        public Criteria andBizIdBetween(String value1, String value2) {
            addCriterion("biz_id between", value1, value2, "bizId");
            return (Criteria) this;
        }

        public Criteria andBizIdNotBetween(String value1, String value2) {
            addCriterion("biz_id not between", value1, value2, "bizId");
            return (Criteria) this;
        }

        public Criteria andSendCountIsNull() {
            addCriterion("send_count is null");
            return (Criteria) this;
        }

        public Criteria andSendCountIsNotNull() {
            addCriterion("send_count is not null");
            return (Criteria) this;
        }

        public Criteria andSendCountEqualTo(String value) {
            addCriterion("send_count =", value, "sendCount");
            return (Criteria) this;
        }

        public Criteria andSendCountNotEqualTo(String value) {
            addCriterion("send_count <>", value, "sendCount");
            return (Criteria) this;
        }

        public Criteria andSendCountGreaterThan(String value) {
            addCriterion("send_count >", value, "sendCount");
            return (Criteria) this;
        }

        public Criteria andSendCountGreaterThanOrEqualTo(String value) {
            addCriterion("send_count >=", value, "sendCount");
            return (Criteria) this;
        }

        public Criteria andSendCountLessThan(String value) {
            addCriterion("send_count <", value, "sendCount");
            return (Criteria) this;
        }

        public Criteria andSendCountLessThanOrEqualTo(String value) {
            addCriterion("send_count <=", value, "sendCount");
            return (Criteria) this;
        }

        public Criteria andSendCountLike(String value) {
            addCriterion("send_count like", value, "sendCount");
            return (Criteria) this;
        }

        public Criteria andSendCountNotLike(String value) {
            addCriterion("send_count not like", value, "sendCount");
            return (Criteria) this;
        }

        public Criteria andSendCountIn(List<String> values) {
            addCriterion("send_count in", values, "sendCount");
            return (Criteria) this;
        }

        public Criteria andSendCountNotIn(List<String> values) {
            addCriterion("send_count not in", values, "sendCount");
            return (Criteria) this;
        }

        public Criteria andSendCountBetween(String value1, String value2) {
            addCriterion("send_count between", value1, value2, "sendCount");
            return (Criteria) this;
        }

        public Criteria andSendCountNotBetween(String value1, String value2) {
            addCriterion("send_count not between", value1, value2, "sendCount");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(String value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(String value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(String value) {
            addCriterion("user_id >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(String value) {
            addCriterion("user_id >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(String value) {
            addCriterion("user_id <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(String value) {
            addCriterion("user_id <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLike(String value) {
            addCriterion("user_id like", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotLike(String value) {
            addCriterion("user_id not like", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<String> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<String> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(String value1, String value2) {
            addCriterion("user_id between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(String value1, String value2) {
            addCriterion("user_id not between", value1, value2, "userId");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}