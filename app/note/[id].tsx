import { IconSymbol } from '@/components/ui/icon-symbol';
import {
  deleteNote,
  loadNotes,
  saveNotes,
  type Note,
  upsertNote,
} from '@/lib/notes-store';
import { router, useLocalSearchParams } from 'expo-router';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Alert, Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

export default function NoteEditorScreen() {
  const params = useLocalSearchParams<{ id: string }>();
  const id = params.id ?? '';

  const [allNotes, setAllNotes] = useState<Note[]>([]);
  const [note, setNote] = useState<Note | null>(null);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [pinned, setPinned] = useState(false);

  useEffect(() => {
    let mounted = true;
    (async () => {
      const stored = await loadNotes();
      if (!mounted) return;
      setAllNotes(stored);
      const existing = stored.find((n) => n.id === id) ?? null;
      setNote(existing);
      setTitle(existing?.title ?? '');
      setContent(existing?.content ?? '');
      setPinned(existing?.pinned ?? false);
    })();
    return () => {
      mounted = false;
    };
  }, [id]);

  const canDelete = useMemo(() => Boolean(note && note.id), [note]);

  const saveAndExit = useCallback(async () => {
    const base = note ?? {
      id,
      title: '',
      content: '',
      createdAt: Date.now(),
      updatedAt: Date.now(),
      color: 'blue' as const,
      pinned: false,
    };

    const updated: Note = {
      ...base,
      title,
      content,
      pinned,
      updatedAt: Date.now(),
    };

    const next = upsertNote(allNotes, updated);
    setAllNotes(next);
    await saveNotes(next);
    router.back();
  }, [allNotes, content, id, note, pinned, title]);

  const onDelete = useCallback(() => {
    if (!note) return;
    Alert.alert('Delete note?', 'This cannot be undone.', [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Delete',
        style: 'destructive',
        onPress: async () => {
          const next = deleteNote(allNotes, note.id);
          setAllNotes(next);
          await saveNotes(next);
          router.back();
        },
      },
    ]);
  }, [allNotes, note]);

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.container}>
        <View style={styles.topBar}>
          <Pressable accessibilityRole="button" onPress={() => router.back()} style={styles.iconBtn}>
            <IconSymbol size={20} name="chevron.left" color="#111111" />
          </Pressable>

          <View style={styles.topBarRight}>
            <Pressable
              accessibilityRole="button"
              onPress={() => setPinned((p) => !p)}
              style={[styles.iconBtn, pinned && styles.iconBtnActive]}>
              <IconSymbol size={18} name={pinned ? 'pin.fill' : 'pin'} color="#111111" />
            </Pressable>
            {canDelete ? (
              <Pressable accessibilityRole="button" onPress={onDelete} style={styles.iconBtn}>
                <IconSymbol size={18} name="trash" color="#111111" />
              </Pressable>
            ) : null}

            <Pressable accessibilityRole="button" onPress={() => void saveAndExit()} style={styles.saveBtn}>
              <Text style={styles.saveBtnText}>Save</Text>
            </Pressable>
          </View>
        </View>

        <TextInput
          value={title}
          onChangeText={setTitle}
          placeholder="Title"
          placeholderTextColor="#9aa0a6"
          style={styles.title}
          returnKeyType="next"
        />

        <TextInput
          value={content}
          onChangeText={setContent}
          placeholder="Write your note..."
          placeholderTextColor="#9aa0a6"
          style={styles.content}
          multiline
          textAlignVertical="top"
        />
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: '#F7EED8' },
  container: { flex: 1, paddingHorizontal: 16, paddingTop: 8 },
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  topBarRight: { flexDirection: 'row', alignItems: 'center', gap: 10 },
  iconBtn: {
    width: 40,
    height: 40,
    borderRadius: 14,
    backgroundColor: '#EFE7D2',
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconBtnActive: {
    backgroundColor: 'rgba(242, 140, 40, 0.28)',
  },
  saveBtn: {
    height: 40,
    paddingHorizontal: 14,
    borderRadius: 16,
    backgroundColor: '#111111',
    alignItems: 'center',
    justifyContent: 'center',
  },
  saveBtnText: { color: '#ffffff', fontWeight: '800', fontSize: 14 },
  title: {
    fontSize: 26,
    fontWeight: '900',
    color: '#111111',
    paddingVertical: 10,
    paddingHorizontal: 6,
  },
  content: {
    flex: 1,
    fontSize: 15,
    color: '#1b1b1b',
    lineHeight: 22,
    paddingHorizontal: 6,
    paddingVertical: 10,
    borderRadius: 18,
    backgroundColor: '#EFE7D2',
  },
});

