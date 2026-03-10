package com.community.common;

/**
 * 缂侇垵宕电划铏规暜閹间礁娅ょ紒? */
public class Constants {

    public static class Role {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
    }

    public static class Gender {
        public static final Integer MALE = 1;
        public static final Integer FEMALE = 0;
    }

    public static class ActivityType {
        public static final String CULTURE = "CULTURE";
        public static final String SPORT = "SPORT";
        public static final String VOLUNTEER = "VOLUNTEER";
        public static final String OTHER = "OTHER";
    }

    public static class NoticeType {
        public static final String NOTICE = "NOTICE";
        public static final String ANNOUNCEMENT = "ANNOUNCEMENT";
        public static final String NEWS = "NEWS";
    }

    public static class OrderType {
        public static final String REPAIR = "REPAIR";
        public static final String COMPLAINT = "COMPLAINT";
        public static final String SUGGESTION = "SUGGESTION";
        public static final String OTHER = "OTHER";
    }

    public static class Priority {
        public static final Integer LOW = 0;
        public static final Integer MEDIUM = 1;
        public static final Integer HIGH = 2;
    }

    public static class WorkOrderStatus {
        public static final Integer PENDING = 0;
        public static final Integer PROCESSING = 1;
        public static final Integer COMPLETED = 2;
        public static final Integer CLOSED = 3;
    }

    public static class UserStatus {
        public static final Integer ENABLED = 1;
        public static final Integer DISABLED = 0;
    }

    public static class VolunteerStatus {
        public static final Integer PENDING = 0;
        public static final Integer APPROVED = 1;
        public static final Integer REJECTED = 2;
    }

    public static class FloatingPopulationStatus {
        public static final Integer IN_RESIDENCE = 1;
        public static final Integer LEFT = 0;
    }

    public static class SecurityHazardType {
        public static final String FIRE = "FIRE";
        public static final String THEFT = "THEFT";
        public static final String TRAFFIC = "TRAFFIC";
        public static final String OTHER = "OTHER";
    }

    public static class SecurityHazardStatus {
        public static final Integer PENDING = 0;
        public static final Integer PROCESSING = 1;
        public static final Integer RESOLVED = 2;
        public static final Integer CLOSED = 3;
    }

    public static class RegistrationStatus {
        public static final Integer REGISTERED = 0;
        public static final Integer CHECKED_IN = 1;
        public static final Integer CANCELLED = 2;
    }

    public static class NoticeStatus {
        public static final Integer DRAFT = 0;
        public static final Integer PUBLISHED = 1;
        public static final Integer UNPUBLISHED = 2;
    }

    public static class AppointmentStatus {
        public static final Integer PENDING_CONFIRMATION = 0;
        public static final Integer CONFIRMED = 1;
        public static final Integer COMPLETED = 2;
        public static final Integer CANCELLED = 3;
    }

    public static class HelpStatus {
        public static final Integer CLOSED = 0;
        public static final Integer ONGOING = 1;
        public static final Integer COMPLETED = 2;
    }

    public static class ResidentStatus {
        public static final Integer ACTIVE = 1;
        public static final Integer MOVED_OUT = 0;
    }

    public static class ResidenceType {
        public static final String OWN = "OWN";
        public static final String RENT = "RENT";
    }

    public static class FamilyRelationType {
        public static final String SPOUSE = "SPOUSE";
        public static final String PARENT = "PARENT";
        public static final String CHILD = "CHILD";
        public static final String SIBLING = "SIBLING";
    }

    public static class PointsChangeType {
        public static final Integer INCOME = 1;
        public static final Integer EXPENSE = -1;
    }

    public static class PointsBusinessType {
        public static final String ACTIVITY_CHECKIN = "ACTIVITY_CHECKIN";
    }

    public static class PointsRule {
        public static final Integer ACTIVITY_CHECKIN_POINTS = 10;
    }

    public static class MessageReadStatus {
        public static final Integer UNREAD = 0;
        public static final Integer READ = 1;
    }

    public static class MessageType {
        public static final String WORK_ORDER = "WORK_ORDER";
        public static final String VOLUNTEER = "VOLUNTEER";
        public static final String ACTIVITY = "ACTIVITY";
        public static final String SYSTEM = "SYSTEM";
    }

    public static class MessageBusinessType {
        public static final String WORK_ORDER = "WORK_ORDER";
        public static final String VOLUNTEER = "VOLUNTEER";
        public static final String ACTIVITY_REGISTRATION = "ACTIVITY_REGISTRATION";
    }
}
