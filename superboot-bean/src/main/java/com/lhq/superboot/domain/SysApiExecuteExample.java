package com.lhq.superboot.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SysApiExecuteExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SysApiExecuteExample() {
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

        public Criteria andExecIdIsNull() {
            addCriterion("exec_id is null");
            return (Criteria) this;
        }

        public Criteria andExecIdIsNotNull() {
            addCriterion("exec_id is not null");
            return (Criteria) this;
        }

        public Criteria andExecIdEqualTo(Integer value) {
            addCriterion("exec_id =", value, "execId");
            return (Criteria) this;
        }

        public Criteria andExecIdNotEqualTo(Integer value) {
            addCriterion("exec_id <>", value, "execId");
            return (Criteria) this;
        }

        public Criteria andExecIdGreaterThan(Integer value) {
            addCriterion("exec_id >", value, "execId");
            return (Criteria) this;
        }

        public Criteria andExecIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("exec_id >=", value, "execId");
            return (Criteria) this;
        }

        public Criteria andExecIdLessThan(Integer value) {
            addCriterion("exec_id <", value, "execId");
            return (Criteria) this;
        }

        public Criteria andExecIdLessThanOrEqualTo(Integer value) {
            addCriterion("exec_id <=", value, "execId");
            return (Criteria) this;
        }

        public Criteria andExecIdIn(List<Integer> values) {
            addCriterion("exec_id in", values, "execId");
            return (Criteria) this;
        }

        public Criteria andExecIdNotIn(List<Integer> values) {
            addCriterion("exec_id not in", values, "execId");
            return (Criteria) this;
        }

        public Criteria andExecIdBetween(Integer value1, Integer value2) {
            addCriterion("exec_id between", value1, value2, "execId");
            return (Criteria) this;
        }

        public Criteria andExecIdNotBetween(Integer value1, Integer value2) {
            addCriterion("exec_id not between", value1, value2, "execId");
            return (Criteria) this;
        }

        public Criteria andExecNameIsNull() {
            addCriterion("exec_name is null");
            return (Criteria) this;
        }

        public Criteria andExecNameIsNotNull() {
            addCriterion("exec_name is not null");
            return (Criteria) this;
        }

        public Criteria andExecNameEqualTo(String value) {
            addCriterion("exec_name =", value, "execName");
            return (Criteria) this;
        }

        public Criteria andExecNameNotEqualTo(String value) {
            addCriterion("exec_name <>", value, "execName");
            return (Criteria) this;
        }

        public Criteria andExecNameGreaterThan(String value) {
            addCriterion("exec_name >", value, "execName");
            return (Criteria) this;
        }

        public Criteria andExecNameGreaterThanOrEqualTo(String value) {
            addCriterion("exec_name >=", value, "execName");
            return (Criteria) this;
        }

        public Criteria andExecNameLessThan(String value) {
            addCriterion("exec_name <", value, "execName");
            return (Criteria) this;
        }

        public Criteria andExecNameLessThanOrEqualTo(String value) {
            addCriterion("exec_name <=", value, "execName");
            return (Criteria) this;
        }

        public Criteria andExecNameLike(String value) {
            addCriterion("exec_name like", value, "execName");
            return (Criteria) this;
        }

        public Criteria andExecNameNotLike(String value) {
            addCriterion("exec_name not like", value, "execName");
            return (Criteria) this;
        }

        public Criteria andExecNameIn(List<String> values) {
            addCriterion("exec_name in", values, "execName");
            return (Criteria) this;
        }

        public Criteria andExecNameNotIn(List<String> values) {
            addCriterion("exec_name not in", values, "execName");
            return (Criteria) this;
        }

        public Criteria andExecNameBetween(String value1, String value2) {
            addCriterion("exec_name between", value1, value2, "execName");
            return (Criteria) this;
        }

        public Criteria andExecNameNotBetween(String value1, String value2) {
            addCriterion("exec_name not between", value1, value2, "execName");
            return (Criteria) this;
        }

        public Criteria andExecCodeIsNull() {
            addCriterion("exec_code is null");
            return (Criteria) this;
        }

        public Criteria andExecCodeIsNotNull() {
            addCriterion("exec_code is not null");
            return (Criteria) this;
        }

        public Criteria andExecCodeEqualTo(String value) {
            addCriterion("exec_code =", value, "execCode");
            return (Criteria) this;
        }

        public Criteria andExecCodeNotEqualTo(String value) {
            addCriterion("exec_code <>", value, "execCode");
            return (Criteria) this;
        }

        public Criteria andExecCodeGreaterThan(String value) {
            addCriterion("exec_code >", value, "execCode");
            return (Criteria) this;
        }

        public Criteria andExecCodeGreaterThanOrEqualTo(String value) {
            addCriterion("exec_code >=", value, "execCode");
            return (Criteria) this;
        }

        public Criteria andExecCodeLessThan(String value) {
            addCriterion("exec_code <", value, "execCode");
            return (Criteria) this;
        }

        public Criteria andExecCodeLessThanOrEqualTo(String value) {
            addCriterion("exec_code <=", value, "execCode");
            return (Criteria) this;
        }

        public Criteria andExecCodeLike(String value) {
            addCriterion("exec_code like", value, "execCode");
            return (Criteria) this;
        }

        public Criteria andExecCodeNotLike(String value) {
            addCriterion("exec_code not like", value, "execCode");
            return (Criteria) this;
        }

        public Criteria andExecCodeIn(List<String> values) {
            addCriterion("exec_code in", values, "execCode");
            return (Criteria) this;
        }

        public Criteria andExecCodeNotIn(List<String> values) {
            addCriterion("exec_code not in", values, "execCode");
            return (Criteria) this;
        }

        public Criteria andExecCodeBetween(String value1, String value2) {
            addCriterion("exec_code between", value1, value2, "execCode");
            return (Criteria) this;
        }

        public Criteria andExecCodeNotBetween(String value1, String value2) {
            addCriterion("exec_code not between", value1, value2, "execCode");
            return (Criteria) this;
        }

        public Criteria andExecClassIsNull() {
            addCriterion("exec_class is null");
            return (Criteria) this;
        }

        public Criteria andExecClassIsNotNull() {
            addCriterion("exec_class is not null");
            return (Criteria) this;
        }

        public Criteria andExecClassEqualTo(String value) {
            addCriterion("exec_class =", value, "execClass");
            return (Criteria) this;
        }

        public Criteria andExecClassNotEqualTo(String value) {
            addCriterion("exec_class <>", value, "execClass");
            return (Criteria) this;
        }

        public Criteria andExecClassGreaterThan(String value) {
            addCriterion("exec_class >", value, "execClass");
            return (Criteria) this;
        }

        public Criteria andExecClassGreaterThanOrEqualTo(String value) {
            addCriterion("exec_class >=", value, "execClass");
            return (Criteria) this;
        }

        public Criteria andExecClassLessThan(String value) {
            addCriterion("exec_class <", value, "execClass");
            return (Criteria) this;
        }

        public Criteria andExecClassLessThanOrEqualTo(String value) {
            addCriterion("exec_class <=", value, "execClass");
            return (Criteria) this;
        }

        public Criteria andExecClassLike(String value) {
            addCriterion("exec_class like", value, "execClass");
            return (Criteria) this;
        }

        public Criteria andExecClassNotLike(String value) {
            addCriterion("exec_class not like", value, "execClass");
            return (Criteria) this;
        }

        public Criteria andExecClassIn(List<String> values) {
            addCriterion("exec_class in", values, "execClass");
            return (Criteria) this;
        }

        public Criteria andExecClassNotIn(List<String> values) {
            addCriterion("exec_class not in", values, "execClass");
            return (Criteria) this;
        }

        public Criteria andExecClassBetween(String value1, String value2) {
            addCriterion("exec_class between", value1, value2, "execClass");
            return (Criteria) this;
        }

        public Criteria andExecClassNotBetween(String value1, String value2) {
            addCriterion("exec_class not between", value1, value2, "execClass");
            return (Criteria) this;
        }

        public Criteria andExecMethodIsNull() {
            addCriterion("exec_method is null");
            return (Criteria) this;
        }

        public Criteria andExecMethodIsNotNull() {
            addCriterion("exec_method is not null");
            return (Criteria) this;
        }

        public Criteria andExecMethodEqualTo(String value) {
            addCriterion("exec_method =", value, "execMethod");
            return (Criteria) this;
        }

        public Criteria andExecMethodNotEqualTo(String value) {
            addCriterion("exec_method <>", value, "execMethod");
            return (Criteria) this;
        }

        public Criteria andExecMethodGreaterThan(String value) {
            addCriterion("exec_method >", value, "execMethod");
            return (Criteria) this;
        }

        public Criteria andExecMethodGreaterThanOrEqualTo(String value) {
            addCriterion("exec_method >=", value, "execMethod");
            return (Criteria) this;
        }

        public Criteria andExecMethodLessThan(String value) {
            addCriterion("exec_method <", value, "execMethod");
            return (Criteria) this;
        }

        public Criteria andExecMethodLessThanOrEqualTo(String value) {
            addCriterion("exec_method <=", value, "execMethod");
            return (Criteria) this;
        }

        public Criteria andExecMethodLike(String value) {
            addCriterion("exec_method like", value, "execMethod");
            return (Criteria) this;
        }

        public Criteria andExecMethodNotLike(String value) {
            addCriterion("exec_method not like", value, "execMethod");
            return (Criteria) this;
        }

        public Criteria andExecMethodIn(List<String> values) {
            addCriterion("exec_method in", values, "execMethod");
            return (Criteria) this;
        }

        public Criteria andExecMethodNotIn(List<String> values) {
            addCriterion("exec_method not in", values, "execMethod");
            return (Criteria) this;
        }

        public Criteria andExecMethodBetween(String value1, String value2) {
            addCriterion("exec_method between", value1, value2, "execMethod");
            return (Criteria) this;
        }

        public Criteria andExecMethodNotBetween(String value1, String value2) {
            addCriterion("exec_method not between", value1, value2, "execMethod");
            return (Criteria) this;
        }

        public Criteria andExecParamIsNull() {
            addCriterion("exec_param is null");
            return (Criteria) this;
        }

        public Criteria andExecParamIsNotNull() {
            addCriterion("exec_param is not null");
            return (Criteria) this;
        }

        public Criteria andExecParamEqualTo(String value) {
            addCriterion("exec_param =", value, "execParam");
            return (Criteria) this;
        }

        public Criteria andExecParamNotEqualTo(String value) {
            addCriterion("exec_param <>", value, "execParam");
            return (Criteria) this;
        }

        public Criteria andExecParamGreaterThan(String value) {
            addCriterion("exec_param >", value, "execParam");
            return (Criteria) this;
        }

        public Criteria andExecParamGreaterThanOrEqualTo(String value) {
            addCriterion("exec_param >=", value, "execParam");
            return (Criteria) this;
        }

        public Criteria andExecParamLessThan(String value) {
            addCriterion("exec_param <", value, "execParam");
            return (Criteria) this;
        }

        public Criteria andExecParamLessThanOrEqualTo(String value) {
            addCriterion("exec_param <=", value, "execParam");
            return (Criteria) this;
        }

        public Criteria andExecParamLike(String value) {
            addCriterion("exec_param like", value, "execParam");
            return (Criteria) this;
        }

        public Criteria andExecParamNotLike(String value) {
            addCriterion("exec_param not like", value, "execParam");
            return (Criteria) this;
        }

        public Criteria andExecParamIn(List<String> values) {
            addCriterion("exec_param in", values, "execParam");
            return (Criteria) this;
        }

        public Criteria andExecParamNotIn(List<String> values) {
            addCriterion("exec_param not in", values, "execParam");
            return (Criteria) this;
        }

        public Criteria andExecParamBetween(String value1, String value2) {
            addCriterion("exec_param between", value1, value2, "execParam");
            return (Criteria) this;
        }

        public Criteria andExecParamNotBetween(String value1, String value2) {
            addCriterion("exec_param not between", value1, value2, "execParam");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNull() {
            addCriterion("create_user is null");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNotNull() {
            addCriterion("create_user is not null");
            return (Criteria) this;
        }

        public Criteria andCreateUserEqualTo(String value) {
            addCriterion("create_user =", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotEqualTo(String value) {
            addCriterion("create_user <>", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThan(String value) {
            addCriterion("create_user >", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThanOrEqualTo(String value) {
            addCriterion("create_user >=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThan(String value) {
            addCriterion("create_user <", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThanOrEqualTo(String value) {
            addCriterion("create_user <=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLike(String value) {
            addCriterion("create_user like", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotLike(String value) {
            addCriterion("create_user not like", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserIn(List<String> values) {
            addCriterion("create_user in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotIn(List<String> values) {
            addCriterion("create_user not in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserBetween(String value1, String value2) {
            addCriterion("create_user between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotBetween(String value1, String value2) {
            addCriterion("create_user not between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIsNull() {
            addCriterion("modify_time is null");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIsNotNull() {
            addCriterion("modify_time is not null");
            return (Criteria) this;
        }

        public Criteria andModifyTimeEqualTo(Date value) {
            addCriterion("modify_time =", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotEqualTo(Date value) {
            addCriterion("modify_time <>", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeGreaterThan(Date value) {
            addCriterion("modify_time >", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("modify_time >=", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeLessThan(Date value) {
            addCriterion("modify_time <", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeLessThanOrEqualTo(Date value) {
            addCriterion("modify_time <=", value, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeIn(List<Date> values) {
            addCriterion("modify_time in", values, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotIn(List<Date> values) {
            addCriterion("modify_time not in", values, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeBetween(Date value1, Date value2) {
            addCriterion("modify_time between", value1, value2, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyTimeNotBetween(Date value1, Date value2) {
            addCriterion("modify_time not between", value1, value2, "modifyTime");
            return (Criteria) this;
        }

        public Criteria andModifyUserIsNull() {
            addCriterion("modify_user is null");
            return (Criteria) this;
        }

        public Criteria andModifyUserIsNotNull() {
            addCriterion("modify_user is not null");
            return (Criteria) this;
        }

        public Criteria andModifyUserEqualTo(String value) {
            addCriterion("modify_user =", value, "modifyUser");
            return (Criteria) this;
        }

        public Criteria andModifyUserNotEqualTo(String value) {
            addCriterion("modify_user <>", value, "modifyUser");
            return (Criteria) this;
        }

        public Criteria andModifyUserGreaterThan(String value) {
            addCriterion("modify_user >", value, "modifyUser");
            return (Criteria) this;
        }

        public Criteria andModifyUserGreaterThanOrEqualTo(String value) {
            addCriterion("modify_user >=", value, "modifyUser");
            return (Criteria) this;
        }

        public Criteria andModifyUserLessThan(String value) {
            addCriterion("modify_user <", value, "modifyUser");
            return (Criteria) this;
        }

        public Criteria andModifyUserLessThanOrEqualTo(String value) {
            addCriterion("modify_user <=", value, "modifyUser");
            return (Criteria) this;
        }

        public Criteria andModifyUserLike(String value) {
            addCriterion("modify_user like", value, "modifyUser");
            return (Criteria) this;
        }

        public Criteria andModifyUserNotLike(String value) {
            addCriterion("modify_user not like", value, "modifyUser");
            return (Criteria) this;
        }

        public Criteria andModifyUserIn(List<String> values) {
            addCriterion("modify_user in", values, "modifyUser");
            return (Criteria) this;
        }

        public Criteria andModifyUserNotIn(List<String> values) {
            addCriterion("modify_user not in", values, "modifyUser");
            return (Criteria) this;
        }

        public Criteria andModifyUserBetween(String value1, String value2) {
            addCriterion("modify_user between", value1, value2, "modifyUser");
            return (Criteria) this;
        }

        public Criteria andModifyUserNotBetween(String value1, String value2) {
            addCriterion("modify_user not between", value1, value2, "modifyUser");
            return (Criteria) this;
        }

        public Criteria andIsEnabledIsNull() {
            addCriterion("is_enabled is null");
            return (Criteria) this;
        }

        public Criteria andIsEnabledIsNotNull() {
            addCriterion("is_enabled is not null");
            return (Criteria) this;
        }

        public Criteria andIsEnabledEqualTo(Integer value) {
            addCriterion("is_enabled =", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledNotEqualTo(Integer value) {
            addCriterion("is_enabled <>", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledGreaterThan(Integer value) {
            addCriterion("is_enabled >", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_enabled >=", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledLessThan(Integer value) {
            addCriterion("is_enabled <", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledLessThanOrEqualTo(Integer value) {
            addCriterion("is_enabled <=", value, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledIn(List<Integer> values) {
            addCriterion("is_enabled in", values, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledNotIn(List<Integer> values) {
            addCriterion("is_enabled not in", values, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledBetween(Integer value1, Integer value2) {
            addCriterion("is_enabled between", value1, value2, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsEnabledNotBetween(Integer value1, Integer value2) {
            addCriterion("is_enabled not between", value1, value2, "isEnabled");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIsNull() {
            addCriterion("is_deleted is null");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIsNotNull() {
            addCriterion("is_deleted is not null");
            return (Criteria) this;
        }

        public Criteria andIsDeletedEqualTo(Integer value) {
            addCriterion("is_deleted =", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotEqualTo(Integer value) {
            addCriterion("is_deleted <>", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedGreaterThan(Integer value) {
            addCriterion("is_deleted >", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedGreaterThanOrEqualTo(Integer value) {
            addCriterion("is_deleted >=", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLessThan(Integer value) {
            addCriterion("is_deleted <", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLessThanOrEqualTo(Integer value) {
            addCriterion("is_deleted <=", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIn(List<Integer> values) {
            addCriterion("is_deleted in", values, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotIn(List<Integer> values) {
            addCriterion("is_deleted not in", values, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedBetween(Integer value1, Integer value2) {
            addCriterion("is_deleted between", value1, value2, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotBetween(Integer value1, Integer value2) {
            addCriterion("is_deleted not between", value1, value2, "isDeleted");
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