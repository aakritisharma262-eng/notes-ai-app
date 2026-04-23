import { IconSymbol } from '@/components/ui/icon-symbol';
import {
  createNote,
  deleteNote,
  formatRelativeDay,
  loadNotes,
  loadViewMode,
  saveNotes,
  saveViewMode,
  type Note,
  type NotesViewMode,
  upsertNote,
} from '@/lib/notes-store';
import { useFocusEffect } from '@react-navigation/native';
import { router } from 'expo-router';
import React, { useCallback, useMemo, useRef, useState } from 'react';
import {
  Alert,
  FlatList,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Swipeable } from 'react-native-gesture-handler';

type NoteListItem = { type: 'note'; note: Note } | { type: 'spacer' };

const CARD_COLORS = {
  blue: '#BFE5FF',
  green: '#C9F4A5',
  purple: '#DCCBFF',
  pink: '#FFD4E2',
  orange: '#FFD2A8',
} as const;

export default function NotesHomeScreen() {
  const [notes, setNotes] = useState<Note[]>([]);
  const [query, setQuery] = useState('');
  const [viewMode, setViewMode] = useState<NotesViewMode>('grid');
  const isHydrated = useRef(false);

  const hydrate = useCallback(async () => {
    const [storedNotes, storedViewMode] = await Promise.all([loadNotes(), loadViewMode()]);
    setNotes(storedNotes);
    setViewMode(storedViewMode);
    isHydrated.current = true;
  }, []);

  useFocusEffect(
    useCallback(() => {
      void hydrate();
    }, [hydrate]),
  );

  const filteredNotes = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return notes;
    return notes.filter((n) => n.title.toLowerCase().includes(q) || n.content.toLowerCase().includes(q));
  }, [notes, query]);

  const sortedNotes = useMemo(() => {
    const copy = filteredNotes.slice();
    copy.sort((a, b) => {
      if (a.pinned !== b.pinned) return a.pinned ? -1 : 1;
      return b.updatedAt - a.updatedAt;
    });
    return copy;
  }, [filteredNotes]);

  const notesCountLabel = useMemo(() => {
    const count = notes.length;
    return `${count} ${count === 1 ? 'note' : 'notes'}`;
  }, [notes.length]);

  const data: NoteListItem[] = useMemo(() => {
    if (viewMode === 'list') return sortedNotes.map((note) => ({ type: 'note', note }));
    const items: NoteListItem[] = sortedNotes.map((note) => ({ type: 'note', note }));
    if (items.length % 2 === 1) items.push({ type: 'spacer' });
    return items;
  }, [sortedNotes, viewMode]);

  const persistNotes = useCallback(async (next: Note[]) => {
    setNotes(next);
    if (!isHydrated.current) return;
    await saveNotes(next);
  }, []);

  const onNew = useCallback(async () => {
    const note = createNote();
    const next = upsertNote(notes, note);
    await persistNotes(next);
    router.push({ pathname: '/note/[id]', params: { id: note.id } });
  }, [notes, persistNotes]);

  const togglePin = useCallback(
    async (note: Note) => {
      const updated: Note = { ...note, pinned: !note.pinned, updatedAt: Date.now() };
      await persistNotes(upsertNote(notes, updated));
    },
    [notes, persistNotes],
  );

  const onDelete = useCallback(
    (note: Note) => {
      Alert.alert('Delete note?', 'This cannot be undone.', [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Delete',
          style: 'destructive',
          onPress: () => {
            void persistNotes(deleteNote(notes, note.id));
          },
        },
      ]);
    },
    [notes, persistNotes],
  );

  const openNoteActions = useCallback(
    (note: Note) => {
      Alert.alert(note.title.trim() ? note.title : 'Untitled', undefined, [
        { text: 'Edit', onPress: () => router.push({ pathname: '/note/[id]', params: { id: note.id } }) },
        { text: note.pinned ? 'Unpin' : 'Pin', onPress: () => void togglePin(note) },
        { text: 'Delete', style: 'destructive', onPress: () => onDelete(note) },
        { text: 'Cancel', style: 'cancel' },
      ]);
    },
    [onDelete, togglePin],
  );

  const onToggleView = useCallback(async (next: NotesViewMode) => {
    setViewMode(next);
    await saveViewMode(next);
  }, []);

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.container}>
        <View style={styles.header}>
          <View style={styles.headerLeft}>
            <View style={styles.menuPill}>
              <IconSymbol size={22} name="line.3.horizontal" color="#ffffff" />
            </View>
            <View style={styles.titleStack}>
              <Text style={styles.headerTitle}>Notes</Text>
              <Text style={styles.headerSubtitle}>{notesCountLabel}</Text>
            </View>
          </View>

          <View style={styles.headerRight}>
            <View style={styles.viewToggle}>
              <Pressable
                accessibilityRole="button"
                onPress={() => void onToggleView('grid')}
                style={[styles.viewToggleBtn, viewMode === 'grid' && styles.viewToggleBtnActive]}>
                <IconSymbol
                  size={18}
                  name="square.grid.2x2.fill"
                  color={viewMode === 'grid' ? '#ffffff' : '#1b1b1b'}
                />
              </Pressable>
              <Pressable
                accessibilityRole="button"
                onPress={() => void onToggleView('list')}
                style={[styles.viewToggleBtn, viewMode === 'list' && styles.viewToggleBtnActive]}>
                <IconSymbol size={18} name="list.bullet" color={viewMode === 'list' ? '#ffffff' : '#1b1b1b'} />
              </Pressable>
            </View>
          </View>
        </View>

        <View style={styles.searchWrap}>
          <View style={styles.searchIcon}>
            <IconSymbol size={18} name="magnifyingglass" color="#9aa0a6" />
          </View>
          <TextInput
            value={query}
            onChangeText={setQuery}
            placeholder="Search notes..."
            placeholderTextColor="#9aa0a6"
            style={styles.search}
            returnKeyType="search"
            clearButtonMode="while-editing"
          />
        </View>

        {sortedNotes.length === 0 ? (
          <View style={styles.empty}>
            <View style={styles.emptyIconWrap}>
              <IconSymbol size={34} name="doc.text" color="#b9b9b9" />
            </View>
            {query.trim().length > 0 ? (
              <>
                <Text style={styles.emptyTitle}>No results</Text>
                <Text style={styles.emptySub}>Try a different search or clear your query.</Text>
                <Pressable accessibilityRole="button" onPress={() => setQuery('')} style={styles.primaryBtn}>
                  <Text style={styles.primaryBtnText}>Clear search</Text>
                </Pressable>
              </>
            ) : (
              <>
                <Text style={styles.emptyTitle}>No notes yet</Text>
                <Text style={styles.emptySub}>Create your first note to get started.</Text>
                <Pressable accessibilityRole="button" onPress={onNew} style={styles.primaryBtn}>
                  <Text style={styles.primaryBtnText}>Create note</Text>
                </Pressable>
              </>
            )}
          </View>
        ) : (
          <FlatList
            data={data}
            key={viewMode}
            keyExtractor={(item, index) => (item.type === 'note' ? item.note.id : `spacer-${index}`)}
            numColumns={viewMode === 'grid' ? 2 : 1}
            columnWrapperStyle={viewMode === 'grid' ? styles.gridRow : undefined}
            contentContainerStyle={styles.listContent}
            renderItem={({ item }) => {
              if (item.type === 'spacer') return <View style={{ flex: 1 }} />;
              const note = item.note;
              const bg = CARD_COLORS[note.color];
              const card = (
                <Pressable
                  onPress={() => router.push({ pathname: '/note/[id]', params: { id: note.id } })}
                  onLongPress={() => openNoteActions(note)}
                  style={[
                    styles.card,
                    viewMode === 'grid' ? styles.cardGrid : styles.cardList,
                    { backgroundColor: bg },
                  ]}>
                  <View style={styles.cardTopRow}>
                    <View style={{ flex: 1 }} />
                    {note.pinned ? (
                      <View style={styles.pinPill}>
                        <IconSymbol size={14} name="pin.fill" color="#111111" />
                      </View>
                    ) : null}
                  </View>
                  <Text style={styles.cardTitle} numberOfLines={2}>
                    {note.title.trim() ? note.title : 'Untitled'}
                  </Text>
                  <Text style={styles.cardBody} numberOfLines={viewMode === 'grid' ? 4 : 2}>
                    {note.content.trim() ? note.content : ' '}
                  </Text>
                  <View style={styles.cardFooter}>
                    <Text style={styles.cardDate}>{formatRelativeDay(note.updatedAt)}</Text>
                  </View>
                </Pressable>
              );

              if (viewMode === 'grid') return card;

              return (
                <Swipeable
                  overshootRight={false}
                  renderRightActions={() => (
                    <View style={styles.swipeActions}>
                      <Pressable
                        accessibilityRole="button"
                        onPress={() => void togglePin(note)}
                        style={[styles.swipeBtn, styles.swipeBtnPin]}>
                        <IconSymbol size={18} name={note.pinned ? 'pin.slash' : 'pin'} color="#ffffff" />
                        <Text style={styles.swipeBtnText}>{note.pinned ? 'Unpin' : 'Pin'}</Text>
                      </Pressable>
                      <Pressable
                        accessibilityRole="button"
                        onPress={() => onDelete(note)}
                        style={[styles.swipeBtn, styles.swipeBtnDelete]}>
                        <IconSymbol size={18} name="trash" color="#ffffff" />
                        <Text style={styles.swipeBtnText}>Delete</Text>
                      </Pressable>
                    </View>
                  )}>
                  {card}
                </Swipeable>
              );
            }}
          />
        )}

        <Pressable accessibilityRole="button" onPress={onNew} style={styles.fab}>
          <Text style={styles.fabText}>+</Text>
        </Pressable>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: '#F7EED8' },
  container: { flex: 1, paddingHorizontal: 16, paddingTop: 8 },

  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  headerLeft: { flexDirection: 'row', alignItems: 'center', gap: 10 },
  menuPill: {
    width: 40,
    height: 40,
    borderRadius: 14,
    backgroundColor: '#F28C28',
    alignItems: 'center',
    justifyContent: 'center',
  },
  titleStack: { gap: 2 },
  headerTitle: { fontSize: 26, fontWeight: '900', color: '#121212', letterSpacing: -0.2 },
  headerSubtitle: { fontSize: 12, color: '#6b6b6b', fontWeight: '600' },
  headerRight: { flexDirection: 'row', alignItems: 'center', gap: 10 },

  viewToggle: {
    flexDirection: 'row',
    borderRadius: 14,
    backgroundColor: '#EFE7D2',
    padding: 4,
    gap: 4,
  },
  viewToggleBtn: {
    width: 36,
    height: 36,
    borderRadius: 12,
    alignItems: 'center',
    justifyContent: 'center',
  },
  viewToggleBtnActive: { backgroundColor: '#111111' },

  searchWrap: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#EFE7D2',
    borderRadius: 18,
    paddingHorizontal: 12,
    height: 46,
    marginBottom: 14,
  },
  searchIcon: { marginRight: 8 },
  search: { flex: 1, fontSize: 15, color: '#1b1b1b' },

  empty: { flex: 1, alignItems: 'center', justifyContent: 'center', paddingBottom: 60 },
  emptyIconWrap: {
    width: 76,
    height: 76,
    borderRadius: 22,
    backgroundColor: '#EFE7D2',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 14,
  },
  emptyTitle: { fontSize: 20, fontWeight: '800', color: '#1b1b1b', marginBottom: 6 },
  emptySub: { fontSize: 13, color: '#6b6b6b' },
  primaryBtn: {
    marginTop: 14,
    height: 44,
    paddingHorizontal: 16,
    borderRadius: 16,
    backgroundColor: '#111111',
    alignItems: 'center',
    justifyContent: 'center',
  },
  primaryBtnText: { color: '#ffffff', fontWeight: '800', fontSize: 14 },

  listContent: { paddingBottom: 110 },
  gridRow: { gap: 12 },

  card: { borderRadius: 24, padding: 16, marginBottom: 12 },
  cardGrid: { flex: 1, minHeight: 170 },
  cardList: { width: '100%', minHeight: 130 },
  cardTopRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', marginBottom: 6 },
  pinPill: {
    width: 28,
    height: 28,
    borderRadius: 12,
    backgroundColor: 'rgba(255,255,255,0.45)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  cardTitle: { fontSize: 20, fontWeight: '900', color: '#151515', marginBottom: 8, letterSpacing: -0.2 },
  cardBody: { fontSize: 13, color: '#2b2b2b', lineHeight: 18, flex: 1 },
  cardFooter: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', marginTop: 10 },
  cardDate: { fontSize: 12, color: '#4e4e4e', fontWeight: '600' },

  swipeActions: {
    flexDirection: 'row',
    alignItems: 'stretch',
    gap: 10,
    marginBottom: 12,
  },
  swipeBtn: {
    width: 88,
    borderRadius: 20,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 10,
    gap: 6,
  },
  swipeBtnPin: { backgroundColor: '#111111' },
  swipeBtnDelete: { backgroundColor: '#E0554A' },
  swipeBtnText: { color: '#ffffff', fontWeight: '800', fontSize: 12 },

  fab: {
    position: 'absolute',
    right: 18,
    bottom: 24,
    width: 56,
    height: 56,
    borderRadius: 20,
    backgroundColor: '#F28C28',
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#000',
    shadowOpacity: 0.18,
    shadowRadius: 16,
    shadowOffset: { width: 0, height: 10 },
    elevation: 8,
  },
  fabText: { color: '#ffffff', fontSize: 30, fontWeight: '900', marginTop: -2 },
});
