package org.emeritus.search.lti.common.lti;

/**
 * The Class Role.
 *
 * @see <a href=
 *      "https://www.imsglobal.org/spec/lti/v1p3/#role-vocabularies">https://www.imsglobal.org/spec/lti/v1p3/#role-vocabularies</a>
 */
public class Role {

  /**
   * The Class System.
   */
  public static class System {

    /** The Constant ADMINISTRATOR. */
    // Core system roles
    public static final String ADMINISTRATOR =
        "http://purl.imsglobal.org/vocab/lis/v2/system/person#Administrator";

    /** The Constant NONE. */
    public static final String NONE = "http://purl.imsglobal.org/vocab/lis/v2/system/person#None";

    /** The Constant ACCOUNT_ADMIN. */
    // Non-core system roles
    public static final String ACCOUNT_ADMIN =
        "http://purl.imsglobal.org/vocab/lis/v2/system/person#AccountAdmin";

    /** The Constant CREATOR. */
    public static final String CREATOR =
        "http://purl.imsglobal.org/vocab/lis/v2/system/person#Creator";

    /** The Constant SYS_ADMIN. */
    public static final String SYS_ADMIN =
        "http://purl.imsglobal.org/vocab/lis/v2/system/person#SysAdmin";

    /** The Constant SYS_SUPPORT. */
    public static final String SYS_SUPPORT =
        "http://purl.imsglobal.org/vocab/lis/v2/system/person#SysSupport";

    /** The Constant USER. */
    public static final String USER = "http://purl.imsglobal.org/vocab/lis/v2/system/person#User";
  }

  /**
   * The Class Institution.
   */
  public static class Institution {

    /** The Constant ADMINISTRATOR. */
    // Core institution roles
    public static final String ADMINISTRATOR =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#Administrator";

    /** The Constant FACULTY. */
    public static final String FACULTY =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#Faculty";

    /** The Constant GUEST. */
    public static final String GUEST =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#Guest";

    /** The Constant NONE. */
    public static final String NONE =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#None";

    /** The Constant OTHER. */
    public static final String OTHER =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#Other";

    /** The Constant STAFF. */
    public static final String STAFF =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#Staff";

    /** The Constant STUDENT. */
    public static final String STUDENT =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#Student";

    /** The Constant ALUMNI. */
    // Non‑core institution roles
    public static final String ALUMNI =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#Alumni";

    /** The Constant INSTRUCTOR. */
    public static final String INSTRUCTOR =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#Instructor";

    /** The Constant LEARNER. */
    public static final String LEARNER =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#Learner";

    /** The Constant MEMBER. */
    public static final String MEMBER =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#Member";

    /** The Constant MENTOR. */
    public static final String MENTOR =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#Mentor";

    /** The Constant OBSERVER. */
    public static final String OBSERVER =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#Observer";

    /** The Constant PROSPECTIVE_STUDENT. */
    public static final String PROSPECTIVE_STUDENT =
        "http://purl.imsglobal.org/vocab/lis/v2/institution/person#ProspectiveStudent";
  }

  /**
   * The Class Context.
   */
  public static class Context {

    /** The Constant ADMINISTRATOR. */
    // Core context roles
    public static final String ADMINISTRATOR =
        "http://purl.imsglobal.org/vocab/lis/v2/membership#Administrator";

    /** The Constant CONTENT_DEVELOPER. */
    public static final String CONTENT_DEVELOPER =
        "http://purl.imsglobal.org/vocab/lis/v2/membership#ContentDeveloper";

    /** The Constant INSTRUCTOR. */
    public static final String INSTRUCTOR =
        "http://purl.imsglobal.org/vocab/lis/v2/membership#Instructor";

    /** The Constant LEARNER. */
    public static final String LEARNER =
        "http://purl.imsglobal.org/vocab/lis/v2/membership#Learner";

    /** The Constant MENTOR. */
    public static final String MENTOR = "http://purl.imsglobal.org/vocab/lis/v2/membership#Mentor";

    /** The Constant MANAGER. */
    // Non‑core context roles
    public static final String MANAGER =
        "http://purl.imsglobal.org/vocab/lis/v2/membership#Manager";

    /** The Constant MEMBER. */
    public static final String MEMBER = "http://purl.imsglobal.org/vocab/lis/v2/membership#Member";

    /** The Constant OFFICER. */
    public static final String OFFICER =
        "http://purl.imsglobal.org/vocab/lis/v2/membership#Officer";
  }
}
