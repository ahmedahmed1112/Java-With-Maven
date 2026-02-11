package afs.student.util;

public final class Validators {
    private Validators() {}

    public static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean isEmail(String s) {
        return notBlank(s) && s.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isPhone(String s) {
        // digits only (عدل المدى حسب فريقكم)
        return notBlank(s) && s.matches("^\\d{9,12}$");
    }

    public static int positiveInt(String s, String msg) {
        if (!notBlank(s)) throw new IllegalArgumentException(msg);
        try {
            int x = Integer.parseInt(s.trim());
            if (x <= 0) throw new IllegalArgumentException(msg);
            return x;
        } catch (Exception e) {
            throw new IllegalArgumentException(msg);
        }
    }
}
