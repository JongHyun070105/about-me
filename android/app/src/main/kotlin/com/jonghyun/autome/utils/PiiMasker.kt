package com.jonghyun.autome.utils

object PiiMasker {
    // 주민/외국인번호 (Hyphen 포함 여부도 처리 가능하도록 단순화)
    private val rrnRegex = Regex("\\b(?:[0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[1,2][0-9]|3[0,1]))-?[1-4][0-9]{6}\\b")
    // 휴대전화번호
    private val phoneRegex = Regex("\\b01[016789]-?[0-9]{3,4}-?[0-9]{4}\\b")
    // 임의의 계좌번호 (한국 은행 포맷 근사치)
    private val accountRegex = Regex("\\b\\d{3,6}-\\d{2,6}-\\d{3,6}\\b")
    // 이메일 주소
    private val emailRegex = Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b")
    // 카드 번호 (13~16자리 숫자, 중간 공백/하이픈 허용)
    private val cardRegex = Regex("\\b(?:\\d[ -]*?){13,16}\\b")

    fun maskText(text: String): String {
        var masked = text
        masked = rrnRegex.replace(masked, "[주민번호 마스킹]")
        masked = phoneRegex.replace(masked, "[전화번호 마스킹]")
        masked = accountRegex.replace(masked, "[계좌번호 마스킹]")
        masked = emailRegex.replace(masked, "[이메일 마스킹]")
        masked = cardRegex.replace(masked, "[카드번호 마스킹]")
        return masked
    }
}
