import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../../../core/native_bridge.dart';
import '../../../core/theme/app_theme.dart';

/// AI 답장 바텀시트
/// 3가지 페르소나(수락, 거절, 모호함)의 AI 생성 답변을 표시합니다.
/// 선택하면 클립보드에 복사됩니다.
class AiReplySheet extends StatefulWidget {
  final String roomId;

  const AiReplySheet({super.key, required this.roomId});

  @override
  State<AiReplySheet> createState() => _AiReplySheetState();
}

class _AiReplySheetState extends State<AiReplySheet>
    with SingleTickerProviderStateMixin {
  List<String> _replies = [];
  bool _isLoading = true;
  late AnimationController _animController;

  static const _personaLabels = ['수락', '거절', '모호함'];
  static const _personaIcons = [
    Icons.check_circle_outline_rounded,
    Icons.cancel_outlined,
    Icons.help_outline_rounded,
  ];
  static const _personaColors = [
    Color(0xFF4CAF50),
    Color(0xFFE57373),
    Color(0xFFFFB74D),
  ];

  @override
  void initState() {
    super.initState();
    _animController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );
    _generateReplies();
  }

  @override
  void dispose() {
    _animController.dispose();
    super.dispose();
  }

  Future<void> _generateReplies() async {
    setState(() => _isLoading = true);
    try {
      final replies = await NativeBridge.generateAiReply(widget.roomId);
      setState(() {
        _replies = replies;
      });
      _animController.forward();
    } finally {
      setState(() => _isLoading = false);
    }
  }

  void _copyToClipboard(String text) {
    Clipboard.setData(ClipboardData(text: text));
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Row(
          children: [
            const Icon(Icons.check_rounded, color: Colors.white, size: 18),
            const SizedBox(width: 8),
            const Text('답장이 클립보드에 복사되었습니다'),
          ],
        ),
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        backgroundColor: AppColors.primary,
        duration: const Duration(seconds: 2),
      ),
    );
    Navigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final bottomPadding = MediaQuery.of(context).viewPadding.bottom;

    return Container(
      decoration: BoxDecoration(
        color: isDark ? AppColors.darkBg : Colors.white,
        borderRadius: const BorderRadius.vertical(top: Radius.circular(24)),
      ),
      padding: EdgeInsets.fromLTRB(20, 12, 20, 16 + bottomPadding),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          // 드래그 핸들
          Container(
            width: 40,
            height: 4,
            decoration: BoxDecoration(
              color: context.dividerColor,
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          const SizedBox(height: 20),

          // 헤더
          Row(
            children: [
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  gradient: const LinearGradient(
                    colors: [AppColors.primary, AppColors.primaryLight],
                  ),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: const Icon(
                  Icons.auto_awesome_rounded,
                  color: Colors.white,
                  size: 20,
                ),
              ),
              const SizedBox(width: 12),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'AI 답장 생성',
                    style: TextStyle(
                      fontSize: 17,
                      fontWeight: FontWeight.w700,
                      color: context.textPrimary,
                    ),
                  ),
                  Text(
                    '대화 맥락 기반 3가지 톤의 답변',
                    style: TextStyle(
                      fontSize: 12,
                      color: context.textTertiary,
                    ),
                  ),
                ],
              ),
              const Spacer(),
              // 재생성 버튼
              IconButton(
                onPressed: _isLoading ? null : _generateReplies,
                icon: Icon(
                  Icons.refresh_rounded,
                  color: _isLoading ? context.textTertiary : AppColors.primary,
                ),
                tooltip: '다시 생성',
              ),
            ],
          ),
          const SizedBox(height: 20),

          // 답변 카드 리스트
          if (_isLoading) ...[
            const SizedBox(height: 40),
            const CircularProgressIndicator(color: AppColors.primary),
            const SizedBox(height: 16),
            Text(
              'AI가 답변을 생성하고 있습니다...',
              style: TextStyle(fontSize: 13, color: context.textSecondary),
            ),
            const SizedBox(height: 40),
          ] else ...[
            for (int i = 0; i < _replies.length && i < 3; i++)
              _buildReplyCard(i, isDark),
          ],
        ],
      ),
    );
  }

  Widget _buildReplyCard(int index, bool isDark) {
    return FadeTransition(
      opacity: CurvedAnimation(
        parent: _animController,
        curve: Interval(index * 0.2, 0.6 + index * 0.2, curve: Curves.easeOut),
      ),
      child: SlideTransition(
        position: Tween<Offset>(
          begin: const Offset(0, 0.2),
          end: Offset.zero,
        ).animate(CurvedAnimation(
          parent: _animController,
          curve: Interval(index * 0.2, 0.6 + index * 0.2, curve: Curves.easeOut),
        )),
        child: GestureDetector(
          onTap: () => _copyToClipboard(_replies[index]),
          child: Container(
            width: double.infinity,
            margin: const EdgeInsets.only(bottom: 10),
            padding: const EdgeInsets.all(14),
            decoration: BoxDecoration(
              color: isDark ? AppColors.darkCard : const Color(0xFFFAF9F6),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(
                color: _personaColors[index].withValues(alpha: 0.3),
                width: 1,
              ),
            ),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // 페르소나 아이콘
                Container(
                  width: 36,
                  height: 36,
                  decoration: BoxDecoration(
                    color: _personaColors[index].withValues(alpha: 0.12),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Icon(
                    _personaIcons[index],
                    color: _personaColors[index],
                    size: 20,
                  ),
                ),
                const SizedBox(width: 12),

                // 텍스트
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        _personaLabels[index],
                        style: TextStyle(
                          fontSize: 12,
                          fontWeight: FontWeight.w700,
                          color: _personaColors[index],
                          letterSpacing: 0.5,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        _replies[index],
                        style: TextStyle(
                          fontSize: 14,
                          height: 1.5,
                          color: context.textPrimary,
                        ),
                      ),
                    ],
                  ),
                ),

                // 복사 아이콘
                Icon(
                  Icons.copy_rounded,
                  size: 16,
                  color: context.textTertiary,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
