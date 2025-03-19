package com.project.posts.data.type;

import java.util.EnumSet;
import java.util.Set;

import lombok.Getter;

@Getter
public enum Role {
	ADMIN("관리자"),
	PROFESSOR("고수"),
	SENIOR("중수"),
	BEGINNER("초보"),
	USER("사용자");

	private final String value;
	private Set<Role> accessibleRoles;

	Role(String value) {
		this.value = value;
	}

	static {
		ADMIN.accessibleRoles = EnumSet.allOf(Role.class);
		PROFESSOR.accessibleRoles = EnumSet.of(PROFESSOR, SENIOR, BEGINNER);
		SENIOR.accessibleRoles = EnumSet.of(SENIOR, BEGINNER);
		BEGINNER.accessibleRoles = EnumSet.of(BEGINNER);
		USER.accessibleRoles = EnumSet.of(USER);
	}

	public boolean canAccess(Role targetRole) {
		return this == ADMIN || accessibleRoles.contains(targetRole);
	}

	public static boolean contains(String role) {
		for (Role r : Role.values()) {
			if (r.name().equalsIgnoreCase(role)) {
				return true;
			}
		}
		return false;
	}
}
