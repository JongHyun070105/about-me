import 'package:flutter/material.dart';

class TxtImportScreen extends StatelessWidget {
  const TxtImportScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('TXT 데이터 Import')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const Icon(Icons.upload_file, size: 64, color: Colors.blueGrey),
            const SizedBox(height: 16),
            const Text(
              '대화 내역 내보내기 파일 읽기',
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            const Text(
              '카카오톡 등의 메신저에서 내보낸 텍스트(.txt) 파일을 선택하면, 앱 내부에서 정규식을 통해 자동 파싱 후 로컬 DB에 적재합니다.',
              textAlign: TextAlign.center,
              style: TextStyle(color: Colors.black54),
            ),
            const SizedBox(height: 32),
            ElevatedButton.icon(
              icon: const Icon(Icons.folder_open),
              label: const Text('파일 선택하기'),
              onPressed: () {
                // TODO: 파일 픽커 구현 및 정규식 Bulk Insert
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('파일 시스템 접근 로직 보완 필요')),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}
