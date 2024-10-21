package org.emeritus.search.lti.common.nrps;

import java.util.List;

/**
 * A Response back from the Names and Roles Provisioning Service. I couldn't find a formal
 * definition of this and it looks like there isn't one so it's modeled on examples from Canvas.
 *
 * @see <a href=
 *      "https://www.imsglobal.org/spec/lti-nrps/v2p0#sharing-of-personal-data">https://www.imsglobal.org/spec/lti-nrps/v2p0#sharing-of-personal-data</a>
 */
public class NRPSResponse {

  /** The id. */
  private String id;

  /** The context. */
  private Context context;

  /** The members. */
  private List<Member> members;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the context.
   *
   * @return the context
   */
  public Context getContext() {
    return context;
  }

  /**
   * Sets the context.
   *
   * @param context the new context
   */
  public void setContext(Context context) {
    this.context = context;
  }

  /**
   * Gets the members.
   *
   * @return the members
   */
  public List<Member> getMembers() {
    return members;
  }

  /**
   * Sets the members.
   *
   * @param members the new members
   */
  public void setMembers(List<Member> members) {
    this.members = members;
  }
}
