package cz.zcu.kiv.nlp.ir;

public record Article(
  String title,
  String author,
  String date,
  String content
) {}