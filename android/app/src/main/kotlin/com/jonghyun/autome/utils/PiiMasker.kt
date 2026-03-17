package com.jonghyun.autome.utils

object PiiMasker {
    // 주민/외국인번호 (Hyphen 포함 여부도 처리 가능하도록 단순화)
    private val rrnRegex = Regex("\\b(?:[0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[1,2][0-9]|3[0,1]))-?[1-4][0-9]{6}\\b")
    // 휴대전화번호
    private val phoneRegex = Regex("\\b01[016789]-?[0-9]{3,4}-?[0-9]{4}\\b")
    // 임의의 계좌번호 (한국 은행 포맷 근사치)
    private val accountRegex = Regex("\\b\\d{3,6}-\\d{2,6}-\\d{3,6}\\b")

    fun maskText(text: String): String {
        var masked = text
        masked = rrnRegex.replace(masked, "[주민번호 마스킹]")
        masked = phoneRegex.replace(masked, "[전화번호 마스킹]")
        masked = accountRegex.replace(masked, "[계좌번호 마스킹]")
        return masked
    }
}
