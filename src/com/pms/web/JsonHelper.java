package com.pms.web;

import com.pms.model.Profile;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JsonHelper
 * Lightweight, zero-dependency JSON serializer and parser for Profile objects.
 * Enables REST API communication without requiring heavy third-party JSON libraries.
 */
public class JsonHelper {

    public static String profileToJson(Profile p) {
        if (p == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        appendField(sb, "id", p.getId(), false);
        appendField(sb, "lastModified", String.valueOf(p.getLastModified()), true);
        appendField(sb, "formattedLastModified", p.getFormattedLastModified(), false);
        appendField(sb, "fullName", p.getFullName(), false);
        appendField(sb, "email", p.getEmail(), false);
        appendField(sb, "phone", p.getPhone(), false);
        appendField(sb, "address", p.getAddress(), false);
        appendField(sb, "careerObjective", p.getCareerObjective(), false);
        appendField(sb, "collegeName", p.getCollegeName(), false);
        appendField(sb, "degreeBranch", p.getDegreeBranch(), false);
        appendField(sb, "graduationYear", p.getGraduationYear(), false);
        appendField(sb, "cgpaPercentage", p.getCgpaPercentage(), false);
        appendField(sb, "technicalSkills", p.getTechnicalSkills(), false);
        appendField(sb, "projects", p.getProjects(), false);
        appendField(sb, "experience", p.getExperience(), false);
        appendField(sb, "certifications", p.getCertifications(), false);
        appendField(sb, "achievements", p.getAchievements(), false);
        appendField(sb, "languages", p.getLanguages(), false);
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    public static String profileListToJson(List<Profile> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                sb.append(profileToJson(list.get(i)));
                if (i < list.size() - 1) {
                    sb.append(",");
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static Profile jsonToProfile(String json) {
        Profile p = new Profile();
        String id = extractField(json, "id");
        if (id != null && !id.isEmpty()) {
            p.setId(id);
        }
        p.setFullName(unescapeJson(extractField(json, "fullName")));
        p.setEmail(unescapeJson(extractField(json, "email")));
        p.setPhone(unescapeJson(extractField(json, "phone")));
        p.setAddress(unescapeJson(extractField(json, "address")));
        p.setCareerObjective(unescapeJson(extractField(json, "careerObjective")));
        p.setCollegeName(unescapeJson(extractField(json, "collegeName")));
        p.setDegreeBranch(unescapeJson(extractField(json, "degreeBranch")));
        p.setGraduationYear(unescapeJson(extractField(json, "graduationYear")));
        p.setCgpaPercentage(unescapeJson(extractField(json, "cgpaPercentage")));
        p.setTechnicalSkills(unescapeJson(extractField(json, "technicalSkills")));
        p.setProjects(unescapeJson(extractField(json, "projects")));
        p.setExperience(unescapeJson(extractField(json, "experience")));
        p.setCertifications(unescapeJson(extractField(json, "certifications")));
        p.setAchievements(unescapeJson(extractField(json, "achievements")));
        p.setLanguages(unescapeJson(extractField(json, "languages")));
        return p;
    }

    private static void appendField(StringBuilder sb, String key, String value, boolean isNumber) {
        sb.append("\"").append(key).append("\":");
        if (isNumber) {
            sb.append(value != null && !value.isEmpty() ? value : "0");
        } else {
            sb.append("\"").append(escapeJson(value)).append("\"");
        }
        sb.append(",");
    }

    public static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    public static String unescapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private static String extractField(String json, String fieldName) {
        if (json == null) return "";
        // Match "fieldName"\s*:\s*"([^"]*)" with escaped characters
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        // Match numbers or null if not quoted
        Pattern numPattern = Pattern.compile("\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*([0-9]+)");
        Matcher numMatcher = numPattern.matcher(json);
        if (numMatcher.find()) {
            return numMatcher.group(1);
        }
        return "";
    }
}
