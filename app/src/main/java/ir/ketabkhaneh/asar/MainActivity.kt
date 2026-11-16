package ir.ketabkhaneh.asar

import android.os.Bundle
import android.net.Uri
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextOverflow

private val Forest = Color(0xFF145B49)
private val Cream = Color(0xFFF5F3EC)
private val Ink = Color(0xFF1D2925)
private val Muted = Color(0xFF68756F)

data class Book(
    val id: Int,
    val title: String,
    val category: String,
    val description: String,
    val attribution: String,
    val publisher: String = "نیازمند بررسی",
    val year: String = "ثبت نشده",
    val isbn: String = "ثبت نشده",
    val pages: String = "ثبت نشده",
    val source: String = "اطلاعات اولیه؛ نیازمند تطبیق با شناسنامه چاپی",
    val sourceUrl: String? = null
)

private val seedBooks = listOf(
    Book(1, "طرح کلی اندیشه اسلامی در قرآن", "تألیف", "مباحث بنیادین اندیشه اسلامی بر پایه آیات قرآن.", "تألیف"),
    Book(2, "پیشوای صادق", "تألیف / تنظیم", "مباحثی درباره زندگی و سیره امام جعفر صادق(ع).", "نوع دقیق تدوین نیازمند بررسی"),
    Book(3, "از ژرفای نماز", "تألیف", "از عناوین شناخته‌شده در فهرست آثار.", "تألیف؛ مشخصات چاپ نیازمند بررسی"),
    Book(4, "صلح امام حسن(ع): پرشکوه‌ترین نرمش قهرمانانه تاریخ", "ترجمه", "ترجمه اثری از شیخ راضی آل‌یاسین.", "ترجمه"),
    Book(5, "آینده در قلمرو اسلام", "ترجمه", "ترجمه اثری از سید قطب.", "ترجمه"),
    Book(6, "خون دلی که لعل شد", "خاطرات و روایت", "روایتی از خاطرات دوران مبارزه و زندان.", "خاطرات؛ انتساب و نقش مؤلف/راوی باید دقیق ثبت شود"),
    Book(7, "همرزمان حسین(ع)", "گردآوری بیانات", "مجموعه‌ای از بیانات درباره مبارزه و سیره امام حسین(ع).", "تدوین از بیانات"),
    Book(8, "جهاد تبیین", "گردآوری بیانات", "مجموعه‌ای موضوعی درباره جهاد تبیین.", "تدوین از بیانات"),
    Book(9, "زن و بازیابی هویت حقیقی", "گردآوری بیانات", "مجموعه‌ای موضوعی درباره زن و هویت.", "تدوین از بیانات"),
    Book(10, "تفسیر سوره حمد", "درس و تفسیر", "درس‌ها و مباحث تفسیری درباره سوره حمد.", "درس‌های ارائه‌شده و تدوین‌شده"),
    Book(11, "تفسیر سوره جمعه", "درس و تفسیر", "مباحث تفسیری درباره سوره جمعه.", "درس‌های ارائه‌شده و تدوین‌شده"),
    Book(12, "تفسیر سوره صف", "درس و تفسیر", "مباحث تفسیری درباره سوره صف.", "درس‌های ارائه‌شده و تدوین‌شده"),
    Book(13, "تفسیر سوره ممتحنه", "درس و تفسیر", "مباحث تفسیری درباره سوره ممتحنه.", "درس‌های ارائه‌شده و تدوین‌شده"),
    Book(14, "تفسیر سوره حشر", "درس و تفسیر", "مباحث تفسیری درباره سوره حشر.", "درس‌های ارائه‌شده و تدوین‌شده"),
    Book(15, "دو امام مجاهد", "گردآوری بیانات", "اثری درباره زندگی و مبارزات امام حسن(ع) و امام حسین(ع).", "تدوین از بیانات"),
    Book(16, "فلسطین", "گردآوری بیانات", "مجموعه مطالب و بیانات درباره مسئله فلسطین.", "تدوین از بیانات"),
    Book(17, "حماسه امام سجاد(ع)", "گردآوری بیانات", "عنوانی در مجموعه آثار موضوعی.", "تدوین از بیانات؛ شناسنامه نشر نیازمند بررسی"),
    Book(18, "امین زبان و ادب پارسی", "گردآوری بیانات", "مجموعه‌ای با محوریت زبان و ادب فارسی.", "تدوین از بیانات"),
    Book(19, "گام دوم انقلاب خطاب به ملت ایران", "بیانیه / مجموعه", "متن بیانیه و محتوای مرتبط با گام دوم انقلاب.", "متن بیانیه و نسخه‌های تدوینی باید تفکیک شوند"),
    Book(20, "بانک جامع نامه‌های مقام معظم رهبری", "مجموعه نامه‌ها", "مجموعه‌ای دیجیتال از نامه‌ها و مکاتبات در سال‌های مختلف؛ نسخه PDF در پایگاه قائمیه برای مطالعه رایگان عرضه شده است.", "گردآوری نامه‌ها؛ پدیدآورنده در شناسنامه دیجیتال: آیت‌الله سیدعلی خامنه‌ای", publisher = "مرکز تحقیقات رایانه‌ای قائمیه اصفهان", year = "۱۳۹۰", pages = "۱۲۴", source = "نسخه PDF رایگان؛ پایگاه اعلام کرده نشر رایگان مطالب و نرم‌افزارهای سایت مجاز است. فایل در منبع اصلی میزبانی می‌شود.", sourceUrl = "https://download.ghbook.ir/downloads.php?file=3607-fa-bank-jame-namehay-magham-moazam-rahbari.pdf&id=3607&packagename=ir.ghbook.reader&platform=site")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LibraryApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryApp() {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("همه") }
    var selected by remember { mutableStateOf<Book?>(null) }
    var saved by remember { mutableStateOf(setOf<Int>()) }
    var showSaved by remember { mutableStateOf(false) }
    var selectedPdf by remember { mutableStateOf<Uri?>(null) }
    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                // Keep read access to the selected document where the provider supports it.
                // The PDF is opened directly from the user's device; no upload is performed.
                selectedPdf = uri
            } catch (_: Exception) {
                selectedPdf = null
            }
        }
    }

    val categories = listOf("همه") + seedBooks.map { it.category }.distinct()
    val filtered = seedBooks.filter {
        (category == "همه" || it.category == category) &&
        (it.title.contains(query, true) || it.description.contains(query, true))
    }.filter { !showSaved || saved.contains(it.id) }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Forest,
            secondary = Color(0xFFB18A4A),
            background = Cream,
            surface = Color.White,
            onBackground = Ink
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("کتابخانه آثار", fontWeight = FontWeight.Bold)
                            Text("فهرست کتاب‌های منتشرشده", fontSize = 11.sp, color = Muted)
                        }
                    },
                    navigationIcon = {
                        if (selected != null) {
                            IconButton(onClick = { selected = null }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSaved = !showSaved }) {
                            Icon(
                                if (showSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "علاقه‌مندی‌ها"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
                )
            },
            containerColor = Cream
        ) { padding ->
            if (selectedPdf != null) {
                PdfReaderScreen(
                    uri = selectedPdf!!,
                    onBack = { selectedPdf = null },
                    modifier = Modifier.padding(padding)
                )
            } else if (selected == null) {
                Column(Modifier.padding(padding).fillMaxSize()) {
                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                            .background(Forest, RoundedCornerShape(18.dp)).padding(18.dp)
                    ) {
                        Text("دانش را جست‌وجو کن", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(5.dp))
                        Text("کتاب‌ها، درس‌ها و مجموعه‌های منتشرشده", color = Color(0xFFDCEBE5), fontSize = 13.sp)
                        Spacer(Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, null, tint = Forest)
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(
                                value = query,
                                onValueChange = { query = it },
                                placeholder = { Text("نام کتاب یا موضوع...") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    androidx.compose.foundation.lazy.LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { item ->
                            FilterChip(
                                selected = category == item,
                                onClick = { category = item },
                                label = { Text(item) }
                            )
                        }
                    }
                    Text(
                        if (showSaved) "علاقه‌مندی‌های من" else "${filtered.size} عنوان در فهرست اولیه",
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                        color = Muted,
                        fontSize = 12.sp
                    )
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filtered, key = { it.id }) { book ->
                            BookCard(
                                book = book,
                                isSaved = saved.contains(book.id),
                                onOpen = { selected = book },
                                onSave = {
                                    saved = if (saved.contains(book.id)) saved - book.id else saved + book.id
                                }
                            )
                        }
                    }
                }
            } else {
                BookDetails(
                    book = selected!!,
                    isSaved = saved.contains(selected!!.id),
                    onSave = {
                        val id = selected!!.id
                        saved = if (saved.contains(id)) saved - id else saved + id
                    },
                    onOpenPdf = { pdfPicker.launch(arrayOf("application/pdf")) },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun BookCard(book: Book, isSaved: Boolean, onOpen: () -> Unit, onSave: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onOpen() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(book.category, color = Forest, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(book.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink)
                Spacer(Modifier.height(4.dp))
                Text(book.description, fontSize = 12.sp, color = Muted, maxLines = 2)
            }
            IconButton(onClick = onSave) {
                Icon(
                    if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "ذخیره",
                    tint = Forest
                )
            }
        }
    }
}

@Composable
fun BookDetails(
    book: Book,
    isSaved: Boolean,
    onSave: () -> Unit,
    onOpenPdf: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().padding(18.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Forest),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("کتاب", color = Color(0xFFDCEBE5), fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text(book.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text(book.category, color = Color(0xFFDCEBE5), fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("معرفی کتاب", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Spacer(Modifier.height(6.dp))
        Text(book.description, color = Muted)
        Spacer(Modifier.height(16.dp))
        Text("شناسنامه کتاب", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Spacer(Modifier.height(8.dp))
        DetailRow("نوع انتساب", book.attribution)
        DetailRow("ناشر", book.publisher)
        DetailRow("سال انتشار", book.year)
        DetailRow("شابک", book.isbn)
        DetailRow("تعداد صفحات", book.pages)
        Spacer(Modifier.height(12.dp))
        Text("وضعیت منبع: ${book.source}", color = Muted, fontSize = 11.sp)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onOpenPdf, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.PictureAsPdf, null)
            Spacer(Modifier.width(8.dp))
            Text("انتخاب و مطالعه فایل PDF")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Icon(if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, null)
            Spacer(Modifier.width(8.dp))
            Text(if (isSaved) "حذف از علاقه‌مندی‌ها" else "افزودن به علاقه‌مندی‌ها")
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "نسخه آغازین: اطلاعات کتاب‌شناختی باید پیش از انتشار عمومی با نسخه چاپی یا منبع رسمی ناشر تطبیق داده شود.",
            color = Muted, fontSize = 11.sp
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Muted, fontSize = 13.sp)
        Text(value, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
    HorizontalDivider(color = Color(0xFFE5E9E5))
}


@Composable
fun PdfReaderScreen(uri: Uri, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var renderer by remember(uri) { mutableStateOf<PdfRenderer?>(null) }
    var descriptor by remember(uri) { mutableStateOf<ParcelFileDescriptor?>(null) }
    var pageIndex by remember(uri) { mutableIntStateOf(0) }
    var pageBitmap by remember(uri) { mutableStateOf<Bitmap?>(null) }
    var error by remember(uri) { mutableStateOf<String?>(null) }
    var pageCount by remember(uri) { mutableIntStateOf(0) }

    DisposableEffect(uri) {
        try {
            val fd = context.contentResolver.openFileDescriptor(uri, "r")
            if (fd == null) {
                error = "امکان باز کردن فایل انتخاب‌شده وجود ندارد."
            } else {
                descriptor = fd
                renderer = PdfRenderer(fd)
                pageCount = renderer?.pageCount ?: 0
                if (pageCount == 0) error = "این فایل صفحه قابل‌نمایشی ندارد."
            }
        } catch (e: Exception) {
            error = "باز کردن PDF ناموفق بود. لطفاً فایل دیگری انتخاب کنید."
        }
        onDispose {
            try { renderer?.close() } catch (_: Exception) {}
            try { descriptor?.close() } catch (_: Exception) {}
            renderer = null
            descriptor = null
        }
    }

    LaunchedEffect(renderer, pageIndex) {
        val current = renderer
        if (current != null && pageIndex in 0 until current.pageCount) {
            try {
                val page = current.openPage(pageIndex)
                val scale = minOf(1600f / page.width, 2200f / page.height, 2f)
                val bmp = Bitmap.createBitmap(
                    (page.width * scale).toInt().coerceAtLeast(1),
                    (page.height * scale).toInt().coerceAtLeast(1),
                    Bitmap.Config.ARGB_8888
                )
                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                pageBitmap = bmp
                error = null
            } catch (_: Exception) {
                error = "نمایش این صفحه ممکن نیست."
                pageBitmap = null
            }
        }
    }

    Column(modifier.fillMaxSize().background(Cream)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) { Text("بازگشت به کتاب") }
            Text(
                if (pageCount > 0) "صفحه ${pageIndex + 1} از $pageCount" else "مطالعه PDF",
                fontWeight = FontWeight.Bold,
                color = Forest
            )
        }
        when {
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(error!!, color = Muted, textAlign = TextAlign.Center, modifier = Modifier.padding(24.dp))
            }
            pageBitmap != null -> {
                Box(
                    Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        bitmap = pageBitmap!!.asImageBitmap(),
                        contentDescription = "صفحه ${pageIndex + 1} فایل PDF",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (pageIndex > 0) pageIndex-- },
                        enabled = pageIndex > 0
                    ) {
                        Icon(Icons.Default.NavigateNext, contentDescription = null)
                        Text("صفحه قبل")
                    }
                    OutlinedButton(
                        onClick = { if (pageIndex < pageCount - 1) pageIndex++ },
                        enabled = pageIndex < pageCount - 1
                    ) {
                        Text("صفحه بعد")
                        Icon(Icons.Default.NavigateBefore, contentDescription = null)
                    }
                }
            }
            else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Forest)
            }
        }
    }
}
