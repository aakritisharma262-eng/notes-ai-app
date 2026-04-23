import AsyncStorage from '@react-native-async-storage/async-storage';
import * as FileSystem from 'expo-file-system/legacy';

export type Note = {
  id: string;
  title: string;
  content: string;
  createdAt: number;
  updatedAt: number;
  color: NoteColor;
  pinned: boolean;
};

export type NoteColor = 'blue' | 'green' | 'purple' | 'pink' | 'orange';

const NOTES_KEY = 'notes:v1';
const VIEW_MODE_KEY = 'notes:viewMode:v1';
const NOTES_FILE = `${FileSystem.documentDirectory ?? ''}notes.v1.json`;
const VIEW_MODE_FILE = `${FileSystem.documentDirectory ?? ''}notes.viewMode.v1.txt`;

export type NotesViewMode = 'grid' | 'list';

export async function loadNotes(): Promise<Note[]> {
  try {
    const raw = await getString(NOTES_KEY, NOTES_FILE);
    if (!raw) return [];
    const parsed = JSON.parse(raw) as unknown;
    if (!Array.isArray(parsed)) return [];
    // lightweight migration: ensure required fields exist
    return (parsed as Array<Partial<Note>>).map((n) => {
      const createdAt = typeof n.createdAt === 'number' ? n.createdAt : Date.now();
      const updatedAt = typeof n.updatedAt === 'number' ? n.updatedAt : createdAt;
      return {
        id: String(n.id ?? `${createdAt}-${Math.random().toString(16).slice(2)}`),
        title: String(n.title ?? ''),
        content: String(n.content ?? ''),
        createdAt,
        updatedAt,
        color: (n.color as NoteColor) ?? pickColor(),
        pinned: Boolean(n.pinned ?? false),
      };
    });
  } catch {
    return [];
  }
}

export async function saveNotes(notes: Note[]): Promise<void> {
  try {
    await setString(NOTES_KEY, NOTES_FILE, JSON.stringify(notes));
  } catch {
    // ignore storage failures (e.g. temporarily unavailable native module)
  }
}

export async function loadViewMode(): Promise<NotesViewMode> {
  try {
    const raw = await getString(VIEW_MODE_KEY, VIEW_MODE_FILE);
    return raw === 'list' ? 'list' : 'grid';
  } catch {
    return 'grid';
  }
}

export async function saveViewMode(mode: NotesViewMode): Promise<void> {
  try {
    await setString(VIEW_MODE_KEY, VIEW_MODE_FILE, mode);
  } catch {
    // ignore storage failures
  }
}

export function createNote(partial?: Partial<Pick<Note, 'title' | 'content' | 'color'>>): Note {
  const now = Date.now();
  return {
    id: `${now}-${Math.random().toString(16).slice(2)}`,
    title: partial?.title ?? '',
    content: partial?.content ?? '',
    createdAt: now,
    updatedAt: now,
    color: partial?.color ?? pickColor(),
    pinned: false,
  };
}

export function upsertNote(notes: Note[], note: Note): Note[] {
  const idx = notes.findIndex((n) => n.id === note.id);
  if (idx === -1) return [note, ...notes];
  const copy = notes.slice();
  copy[idx] = note;
  return copy;
}

export function deleteNote(notes: Note[], id: string): Note[] {
  return notes.filter((n) => n.id !== id);
}

export function formatRelativeDay(ts: number): string {
  const dayMs = 24 * 60 * 60 * 1000;
  const now = Date.now();
  const diffDays = Math.floor((stripTime(now) - stripTime(ts)) / dayMs);
  if (diffDays <= 0) return 'Today';
  if (diffDays === 1) return 'Yesterday';
  return `${diffDays} days ago`;
}

function stripTime(ts: number): number {
  const d = new Date(ts);
  d.setHours(0, 0, 0, 0);
  return d.getTime();
}

function pickColor(): NoteColor {
  const colors: NoteColor[] = ['blue', 'green', 'purple', 'pink', 'orange'];
  return colors[Math.floor(Math.random() * colors.length)] ?? 'blue';
}

async function getString(key: string, fileUri: string): Promise<string | null> {
  try {
    const raw = await AsyncStorage.getItem(key);
    if (raw != null) return raw;
  } catch {
    // fall back to file
  }

  try {
    if (!fileUri) return null;
    const info = await FileSystem.getInfoAsync(fileUri);
    if (!info.exists) return null;
    return await FileSystem.readAsStringAsync(fileUri);
  } catch {
    return null;
  }
}

async function setString(key: string, fileUri: string, value: string): Promise<void> {
  try {
    await AsyncStorage.setItem(key, value);
    return;
  } catch {
    // fall back to file
  }

  if (!fileUri) return;
  try {
    await FileSystem.writeAsStringAsync(fileUri, value);
  } catch {
    // ignore
  }
}
