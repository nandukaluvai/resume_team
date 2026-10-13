/**
 * Profile Management System - Web Frontend Application Logic
 * Implements Live Template Switching (6 Themes), Real-time Preview,
 * Form Validation, Local Storage & Server API Synchronization, and PDFBox Export.
 */

// Application State
const state = {
    profiles: [],
    activeProfile: null,
    activeTemplate: 'PROFESSIONAL',
    zoom: 1.0,
    editingId: null,
    deleteTargetId: null
};

// DOM Content Loaded Entry Point
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initForm();
    initPreviewControls();
    initManageSection();
    initModals();

    // Initial Data Fetch from Java Backend
    loadProfiles();
});

// ==========================================================================
// 1. Navigation & Tab Switching
// ==========================================================================
function initNavigation() {
    const navButtons = document.querySelectorAll('.nav-btn');
    navButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabId = btn.getAttribute('data-tab');
            switchTab(tabId);
        });
    });

    // Brand logo returns to Dashboard
    document.getElementById('brand-logo').addEventListener('click', () => switchTab('tab-home'));

    // Dashboard Hero Buttons
    document.getElementById('btn-hero-create').addEventListener('click', () => {
        resetForm();
        switchTab('tab-builder');
    });
    document.getElementById('btn-hero-manage').addEventListener('click', () => switchTab('tab-manage'));

    // Top Header Sample Loader
    document.getElementById('btn-quick-sample').addEventListener('click', async () => {
        await fetchAndApplySample(true);
    });

    // Dashboard Template Chips
    document.querySelectorAll('.template-chip').forEach(chip => {
        chip.addEventListener('click', () => {
            const tpl = chip.getAttribute('data-tpl');
            document.querySelectorAll('.template-chip').forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
            state.activeTemplate = tpl;
            document.getElementById('preview-template-select').value = tpl;
            switchTab('tab-preview');
        });
    });
}

function switchTab(tabId) {
    document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));

    const targetPane = document.getElementById(tabId);
    if (targetPane) targetPane.classList.add('active');

    const targetNav = document.querySelector(`[data-tab="${tabId}"]`);
    if (targetNav) targetNav.classList.add('active');

    // Tab-specific lifecycle actions
    if (tabId === 'tab-preview') {
        renderActiveResume();
    } else if (tabId === 'tab-manage') {
        renderProfilesTable();
    }
}

// ==========================================================================
// 2. Data Communication with Java Backend (REST API)
// ==========================================================================
async function loadProfiles() {
    try {
        const res = await fetch('/api/profiles');
        if (res.ok) {
            state.profiles = await res.json();
            updateStatsBadge();
            renderProfilesTable();

            // Set active profile if not yet set
            if (!state.activeProfile && state.profiles.length > 0) {
                state.activeProfile = state.profiles[0];
            } else if (!state.activeProfile) {
                // If backend has no profiles yet, fetch sample
                await fetchAndApplySample(false);
            }
        }
    } catch (err) {
        console.warn('Backend offline or initializing:', err);
        // Fallback sample if server not reachable yet
        await fetchAndApplySample(false);
    }
}

async function fetchAndApplySample(triggerToast = true) {
    try {
        const res = await fetch('/api/sample');
        if (res.ok) {
            const sample = await res.json();
            state.activeProfile = sample;
            populateFormWithProfile(sample);
            renderActiveResume();
            if (triggerToast) showToast('Sample Engineering profile loaded!', 'info');
        }
    } catch (e) {
        console.error('Failed to load sample:', e);
    }
}

function updateStatsBadge() {
    const el = document.getElementById('stat-profile-count');
    if (el) el.textContent = state.profiles.length;
}

// ==========================================================================
// 3. Resume Builder Form Handling
// ==========================================================================
function initForm() {
    const form = document.getElementById('profile-form');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const saved = await saveCurrentProfile();
        if (saved) {
            switchTab('tab-preview');
        }
    });

    document.getElementById('btn-save-only').addEventListener('click', async () => {
        await saveCurrentProfile();
    });

    document.getElementById('btn-form-fill-sample').addEventListener('click', async () => {
        await fetchAndApplySample(true);
    });

    document.getElementById('btn-form-reset').addEventListener('click', () => {
        resetForm();
        showToast('Form cleared', 'info');
    });

    document.getElementById('form-template-choice').addEventListener('change', (e) => {
        state.activeTemplate = e.target.value;
        document.getElementById('preview-template-select').value = e.target.value;
    });
}

function resetForm() {
    state.editingId = null;
    document.getElementById('field-id').value = '';
    document.getElementById('form-heading').textContent = 'Candidate Profile Form';
    document.querySelectorAll('#profile-form input, #profile-form textarea').forEach(el => {
        if (el.type !== 'hidden') el.value = '';
    });
    clearFieldErrors();
}

function clearFieldErrors() {
    document.querySelectorAll('.field-error').forEach(el => el.textContent = '');
}

function validateForm(data) {
    clearFieldErrors();
    let valid = true;

    if (!data.fullName || data.fullName.trim().length < 2) {
        document.getElementById('err-fullName').textContent = 'Full name is required (min 2 chars).';
        valid = false;
    }

    const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    if (!data.email || !emailRegex.test(data.email.trim())) {
        document.getElementById('err-email').textContent = 'Enter a valid email (e.g. name@domain.com).';
        valid = false;
    }

    const phoneRegex = /^[+]?[0-9\s\-()]{7,18}$/;
    if (!data.phone || !phoneRegex.test(data.phone.trim())) {
        document.getElementById('err-phone').textContent = 'Enter a valid phone number (7 to 15 digits).';
        valid = false;
    }

    if (!data.address || data.address.trim().length < 2) {
        document.getElementById('err-address').textContent = 'Location/Address is required.';
        valid = false;
    }

    if (!data.careerObjective || data.careerObjective.trim().length < 10) {
        document.getElementById('err-careerObjective').textContent = 'Career objective is required (min 10 chars).';
        valid = false;
    }

    if (!data.collegeName || data.collegeName.trim().length < 2) {
        document.getElementById('err-collegeName').textContent = 'College name is required.';
        valid = false;
    }

    if (!data.degreeBranch || data.degreeBranch.trim().length < 2) {
        document.getElementById('err-degreeBranch').textContent = 'Degree & branch is required.';
        valid = false;
    }

    const year = parseInt(data.graduationYear, 10);
    if (!year || year < 1980 || year > 2040) {
        document.getElementById('err-graduationYear').textContent = 'Enter valid 4-digit graduation year (1980-2040).';
        valid = false;
    }

    if (!data.cgpaPercentage || data.cgpaPercentage.trim().length < 1) {
        document.getElementById('err-cgpaPercentage').textContent = 'CGPA or score is required.';
        valid = false;
    }

    if (!data.technicalSkills || data.technicalSkills.trim().length < 2) {
        document.getElementById('err-technicalSkills').textContent = 'Please enter at least one technical skill.';
        valid = false;
    }

    return valid;
}

function getFormData() {
    return {
        id: document.getElementById('field-id').value || null,
        fullName: document.getElementById('field-fullName').value.trim(),
        email: document.getElementById('field-email').value.trim(),
        phone: document.getElementById('field-phone').value.trim(),
        address: document.getElementById('field-address').value.trim(),
        careerObjective: document.getElementById('field-careerObjective').value.trim(),
        collegeName: document.getElementById('field-collegeName').value.trim(),
        degreeBranch: document.getElementById('field-degreeBranch').value.trim(),
        graduationYear: document.getElementById('field-graduationYear').value.trim(),
        cgpaPercentage: document.getElementById('field-cgpaPercentage').value.trim(),
        technicalSkills: document.getElementById('field-technicalSkills').value.trim(),
        projects: document.getElementById('field-projects').value.trim(),
        experience: document.getElementById('field-experience').value.trim(),
        certifications: document.getElementById('field-certifications').value.trim(),
        achievements: document.getElementById('field-achievements').value.trim(),
        languages: document.getElementById('field-languages').value.trim()
    };
}

function populateFormWithProfile(p) {
    if (!p) return;
    state.editingId = p.id;
    document.getElementById('field-id').value = p.id || '';
    document.getElementById('field-fullName').value = p.fullName || '';
    document.getElementById('field-email').value = p.email || '';
    document.getElementById('field-phone').value = p.phone || '';
    document.getElementById('field-address').value = p.address || '';
    document.getElementById('field-careerObjective').value = p.careerObjective || '';
    document.getElementById('field-collegeName').value = p.collegeName || '';
    document.getElementById('field-degreeBranch').value = p.degreeBranch || '';
    document.getElementById('field-graduationYear').value = p.graduationYear || '';
    document.getElementById('field-cgpaPercentage').value = p.cgpaPercentage || '';
    document.getElementById('field-technicalSkills').value = p.technicalSkills || '';
    document.getElementById('field-projects').value = p.projects || '';
    document.getElementById('field-experience').value = p.experience || '';
    document.getElementById('field-certifications').value = p.certifications || '';
    document.getElementById('field-achievements').value = p.achievements || '';
    document.getElementById('field-languages').value = p.languages || '';

    document.getElementById('form-heading').textContent = 'Edit Profile: ' + (p.fullName || 'Candidate');
}

async function saveCurrentProfile() {
    const data = getFormData();
    if (!validateForm(data)) {
        showToast('Please fix the validation errors in the form.', 'error');
        return false;
    }

    try {
        const isUpdate = !!data.id;
        const method = isUpdate ? 'PUT' : 'POST';
        const res = await fetch('/api/profiles', {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (res.ok) {
            const savedProfile = await res.json();
            state.activeProfile = savedProfile;
            state.editingId = savedProfile.id;
            document.getElementById('field-id').value = savedProfile.id;

            showToast(`Profile for '${savedProfile.fullName}' saved successfully!`, 'success');
            await loadProfiles();
            return true;
        } else {
            const err = await res.json();
            showToast(err.error || 'Failed to save profile.', 'error');
            return false;
        }
    } catch (err) {
        console.warn('Backend offline, saving to browser local storage:', err);
        if (!data.id) data.id = 'p-' + Date.now();
        data.formattedLastModified = 'Saved locally';
        data.lastModified = Date.now();
        const existingIdx = state.profiles.findIndex(p => p.id === data.id);
        if (existingIdx >= 0) {
            state.profiles[existingIdx] = data;
        } else {
            state.profiles.unshift(data);
        }
        try { localStorage.setItem('pms_local_profiles', JSON.stringify(state.profiles)); } catch(e){}
        state.activeProfile = data;
        state.editingId = data.id;
        document.getElementById('field-id').value = data.id;
        updateStatsBadge();
        renderProfilesTable();
        showToast(`Profile for '${data.fullName}' saved!`, 'success');
        return true;
    }
}

// ==========================================================================
// 4. Live Resume Preview & Template Switcher
// ==========================================================================
function initPreviewControls() {
    const tplSelect = document.getElementById('preview-template-select');
    tplSelect.addEventListener('change', (e) => {
        state.activeTemplate = e.target.value;
        document.getElementById('preview-template-tag').textContent = tplSelect.options[tplSelect.selectedIndex].text.split('.')[1].trim();
        renderActiveResume();
    });

    // Zoom Controls
    document.getElementById('btn-zoom-in').addEventListener('click', () => adjustZoom(0.1));
    document.getElementById('btn-zoom-out').addEventListener('click', () => adjustZoom(-0.1));
    document.getElementById('btn-zoom-reset').addEventListener('click', () => {
        state.zoom = 1.0;
        applyZoom();
    });

    // Edit active profile button
    document.getElementById('btn-edit-active-profile').addEventListener('click', () => {
        if (state.activeProfile) {
            populateFormWithProfile(state.activeProfile);
            switchTab('tab-builder');
        }
    });

    // Print button
    document.getElementById('btn-print-resume').addEventListener('click', () => {
        window.print();
    });

    // Export PDF via Java Backend (or print fallback)
    document.getElementById('btn-export-pdf').addEventListener('click', () => {
        triggerDownloadCurrentPDF();
    });
}

function adjustZoom(delta) {
    state.zoom = Math.max(0.6, Math.min(1.5, state.zoom + delta));
    applyZoom();
}

function applyZoom() {
    const sheet = document.getElementById('resume-sheet');
    const label = document.getElementById('lbl-zoom-level');
    sheet.style.transform = `scale(${state.zoom})`;
    label.textContent = `${Math.round(state.zoom * 100)}%`;
}

function triggerDownloadCurrentPDF() {
    if (!state.activeProfile) {
        showToast('No active profile to export.', 'error');
        return;
    }
    const profileId = state.activeProfile.id || '';
    const template = state.activeTemplate || 'PROFESSIONAL';
    const downloadUrl = `/api/export-pdf?id=${encodeURIComponent(profileId)}&template=${encodeURIComponent(template)}`;

    showToast('Compiling vector PDF via Apache PDFBox...', 'info');
    fetch(downloadUrl, { method: 'HEAD' })
        .then(res => {
            if (res.ok) {
                window.open(downloadUrl, '_blank');
            } else {
                showToast('Opening print dialog to Save as PDF...', 'info');
                window.print();
            }
        })
        .catch(() => {
            showToast('Opening print dialog to Save as PDF...', 'info');
            window.print();
        });
}

// ==========================================================================
// 5. Template Renderers (Polymorphic HTML synthesis)
// ==========================================================================
function renderActiveResume() {
    const container = document.getElementById('resume-content-render');
    const p = state.activeProfile;
    if (!p) {
        container.innerHTML = '<div style="padding:40px; text-align:center; color:#94a3b8;">No profile selected. Click "Load Sample Profile" to preview.</div>';
        return;
    }

    const tpl = state.activeTemplate;
    let html = '';

    switch (tpl) {
        case 'MODERN':
            html = renderModernTemplate(p);
            break;
        case 'SIMPLE':
            html = renderSimpleTemplate(p);
            break;
        case 'STUDENT_FRESHER':
            html = renderStudentFresherTemplate(p);
            break;
        case 'TECHNICAL':
            html = renderTechnicalTemplate(p);
            break;
        case 'CREATIVE':
            html = renderCreativeTemplate(p);
            break;
        case 'PROFESSIONAL':
        default:
            html = renderProfessionalTemplate(p);
            break;
    }

    container.innerHTML = html;
}

// Template 1: Professional (Corporate Navy Banner)
function renderProfessionalTemplate(p) {
    return `
    <div class="tpl-professional">
        <header class="pro-header">
            <h1 class="pro-name">${escapeHtml(p.fullName)}</h1>
            <div class="pro-degree">${escapeHtml(p.degreeBranch)}</div>
            <div class="pro-contact">
                ✉ ${escapeHtml(p.email)} &nbsp;|&nbsp; ☎ ${escapeHtml(p.phone)} &nbsp;|&nbsp; 📍 ${escapeHtml(p.address)}
            </div>
        </header>
        <div class="pro-body">
            <section class="pro-section">
                <div class="pro-section-title">Career Objective</div>
                <div class="pro-section-content">${escapeHtml(p.careerObjective)}</div>
            </section>

            <section class="pro-section">
                <div class="pro-section-title">Education</div>
                <div class="pro-edu-card">
                    <strong>${escapeHtml(p.collegeName)}</strong>
                    <span style="font-weight:600;">Graduation: ${escapeHtml(p.graduationYear)}</span>
                </div>
                <div class="pro-edu-card" style="color:#64748b;">
                    <span>${escapeHtml(p.degreeBranch)}</span>
                    <strong style="color:#1e3a8a;">Score: ${escapeHtml(p.cgpaPercentage)}</strong>
                </div>
            </section>

            ${p.technicalSkills ? `
            <section class="pro-section">
                <div class="pro-section-title">Technical Skills</div>
                <div class="pro-section-content">${escapeHtml(p.technicalSkills)}</div>
            </section>` : ''}

            ${p.projects ? `
            <section class="pro-section">
                <div class="pro-section-title">Software Projects</div>
                <div class="pro-section-content">${escapeHtml(p.projects)}</div>
            </section>` : ''}

            ${p.experience ? `
            <section class="pro-section">
                <div class="pro-section-title">Work Experience / Internships</div>
                <div class="pro-section-content">${escapeHtml(p.experience)}</div>
            </section>` : ''}

            ${(p.certifications || p.achievements) ? `
            <section class="pro-section">
                <div class="pro-section-title">Certifications & Achievements</div>
                <div class="pro-section-content">
                    ${p.certifications ? `<strong>Certifications:</strong>\n${escapeHtml(p.certifications)}\n\n` : ''}
                    ${p.achievements ? `<strong>Achievements:</strong>\n${escapeHtml(p.achievements)}` : ''}
                </div>
            </section>` : ''}

            ${p.languages ? `
            <section class="pro-section">
                <div class="pro-section-title">Languages Known</div>
                <div class="pro-section-content">${escapeHtml(p.languages)}</div>
            </section>` : ''}
        </div>
    </div>`;
}

// Template 2: Modern (Two-Column Sidebar Layout)
function renderModernTemplate(p) {
    const initials = getInitials(p.fullName);
    return `
    <div class="tpl-modern">
        <aside class="mod-sidebar">
            <div class="mod-avatar-circle">${initials}</div>

            <div class="mod-side-heading">Contact</div>
            <div class="mod-side-label">Email</div>
            <div class="mod-side-item">${escapeHtml(p.email)}</div>
            <div class="mod-side-label">Phone</div>
            <div class="mod-side-item">${escapeHtml(p.phone)}</div>
            <div class="mod-side-label">Location</div>
            <div class="mod-side-item">${escapeHtml(p.address)}</div>

            <div class="mod-side-heading">Education</div>
            <div style="font-weight:700; font-size:11px;">${escapeHtml(p.degreeBranch)}</div>
            <div style="font-size:10px; color:#cbd5e1; margin-top:2px;">${escapeHtml(p.collegeName)}</div>
            <div style="font-size:10px; color:#06b6d4; font-weight:700; margin-top:4px;">
                Class of ${escapeHtml(p.graduationYear)} &bull; ${escapeHtml(p.cgpaPercentage)}
            </div>

            ${p.technicalSkills ? `
            <div class="mod-side-heading">Core Skills</div>
            <div class="mod-side-item">${escapeHtml(p.technicalSkills)}</div>` : ''}

            ${p.languages ? `
            <div class="mod-side-heading">Languages</div>
            <div class="mod-side-item">${escapeHtml(p.languages)}</div>` : ''}
        </aside>

        <main class="mod-main">
            <h1 class="mod-name">${escapeHtml(p.fullName)}</h1>
            <div class="mod-track">${escapeHtml(p.degreeBranch)}</div>
            <div class="mod-divider"></div>

            <section class="mod-main-section">
                <div class="mod-main-title">Profile Summary</div>
                <div class="mod-title-line"></div>
                <div class="mod-main-content">${escapeHtml(p.careerObjective)}</div>
            </section>

            ${p.experience ? `
            <section class="mod-main-section">
                <div class="mod-main-title">Experience & Internships</div>
                <div class="mod-title-line"></div>
                <div class="mod-main-content">${escapeHtml(p.experience)}</div>
            </section>` : ''}

            ${p.projects ? `
            <section class="mod-main-section">
                <div class="mod-main-title">Key Projects</div>
                <div class="mod-title-line"></div>
                <div class="mod-main-content">${escapeHtml(p.projects)}</div>
            </section>` : ''}

            ${p.certifications ? `
            <section class="mod-main-section">
                <div class="mod-main-title">Certifications</div>
                <div class="mod-title-line"></div>
                <div class="mod-main-content">${escapeHtml(p.certifications)}</div>
            </section>` : ''}

            ${p.achievements ? `
            <section class="mod-main-section">
                <div class="mod-main-title">Achievements & Honors</div>
                <div class="mod-title-line"></div>
                <div class="mod-main-content">${escapeHtml(p.achievements)}</div>
            </section>` : ''}
        </main>
    </div>`;
}

// Template 3: Simple (ATS Minimalist Monochrome)
function renderSimpleTemplate(p) {
    return `
    <div class="tpl-simple">
        <header class="sim-header">
            <h1 class="sim-name">${escapeHtml(p.fullName)}</h1>
            <div class="sim-contact">
                ${escapeHtml(p.email)} &nbsp;&bull;&nbsp; ${escapeHtml(p.phone)} &nbsp;&bull;&nbsp; ${escapeHtml(p.address)}
            </div>
        </header>

        <section class="sim-section">
            <div class="sim-heading">Career Objective</div>
            <div class="sim-content">${escapeHtml(p.careerObjective)}</div>
        </section>

        <section class="sim-section">
            <div class="sim-heading">Education</div>
            <div style="display:flex; justify-content:space-between;">
                <strong>${escapeHtml(p.degreeBranch)}</strong>
                <span>Graduation: ${escapeHtml(p.graduationYear)}</span>
            </div>
            <div style="display:flex; justify-content:space-between; color:#4b5563;">
                <span>${escapeHtml(p.collegeName)}</span>
                <strong>CGPA/Score: ${escapeHtml(p.cgpaPercentage)}</strong>
            </div>
        </section>

        ${p.technicalSkills ? `
        <section class="sim-section">
            <div class="sim-heading">Technical Skills</div>
            <div class="sim-content">${escapeHtml(p.technicalSkills)}</div>
        </section>` : ''}

        ${p.projects ? `
        <section class="sim-section">
            <div class="sim-heading">Projects</div>
            <div class="sim-content">${escapeHtml(p.projects)}</div>
        </section>` : ''}

        ${p.experience ? `
        <section class="sim-section">
            <div class="sim-heading">Experience</div>
            <div class="sim-content">${escapeHtml(p.experience)}</div>
        </section>` : ''}

        ${p.certifications ? `
        <section class="sim-section">
            <div class="sim-heading">Certifications</div>
            <div class="sim-content">${escapeHtml(p.certifications)}</div>
        </section>` : ''}

        ${p.achievements ? `
        <section class="sim-section">
            <div class="sim-heading">Achievements</div>
            <div class="sim-content">${escapeHtml(p.achievements)}</div>
        </section>` : ''}

        ${p.languages ? `
        <section class="sim-section">
            <div class="sim-heading">Languages</div>
            <div class="sim-content">${escapeHtml(p.languages)}</div>
        </section>` : ''}
    </div>`;
}

// Template 4: Student / Fresher (Academic Emerald / Teal)
function renderStudentFresherTemplate(p) {
    return `
    <div class="tpl-student_fresher">
        <div class="fre-stripe"></div>
        <h1 class="fre-name">${escapeHtml(p.fullName)}</h1>
        <div class="fre-degree">${escapeHtml(p.degreeBranch)}</div>
        <div class="fre-contact">
            ✉ ${escapeHtml(p.email)} &nbsp;|&nbsp; ☎ ${escapeHtml(p.phone)} &nbsp;|&nbsp; 📍 ${escapeHtml(p.address)}
        </div>

        <div class="fre-edu-box">
            <div>
                <strong style="color:#134e4a; font-size:13px;">🎓 ${escapeHtml(p.collegeName)}</strong>
                <div style="font-size:12px; color:#475569; margin-top:2px;">${escapeHtml(p.degreeBranch)}</div>
            </div>
            <div class="fre-badge">
                CGPA: ${escapeHtml(p.cgpaPercentage)} &bull; Class of ${escapeHtml(p.graduationYear)}
            </div>
        </div>

        <section class="fre-section">
            <div class="fre-heading"><span class="fre-dot"></span> Career Objective</div>
            <div class="fre-content">${escapeHtml(p.careerObjective)}</div>
        </section>

        ${p.technicalSkills ? `
        <section class="fre-section">
            <div class="fre-heading"><span class="fre-dot"></span> Core Technical Skills</div>
            <div class="fre-content">${escapeHtml(p.technicalSkills)}</div>
        </section>` : ''}

        ${p.projects ? `
        <section class="fre-section">
            <div class="fre-heading"><span class="fre-dot"></span> Academic & Software Projects</div>
            <div class="fre-content">${escapeHtml(p.projects)}</div>
        </section>` : ''}

        ${p.experience ? `
        <section class="fre-section">
            <div class="fre-heading"><span class="fre-dot"></span> Internships & Practical Training</div>
            <div class="fre-content">${escapeHtml(p.experience)}</div>
        </section>` : ''}

        ${p.certifications ? `
        <section class="fre-section">
            <div class="fre-heading"><span class="fre-dot"></span> Certifications & Courses</div>
            <div class="fre-content">${escapeHtml(p.certifications)}</div>
        </section>` : ''}

        ${p.achievements ? `
        <section class="fre-section">
            <div class="fre-heading"><span class="fre-dot"></span> Awards & Honors</div>
            <div class="fre-content">${escapeHtml(p.achievements)}</div>
        </section>` : ''}

        ${p.languages ? `
        <section class="fre-section">
            <div class="fre-heading"><span class="fre-dot"></span> Languages Known</div>
            <div class="fre-content">${escapeHtml(p.languages)}</div>
        </section>` : ''}
    </div>`;
}

// Template 5: Technical (Developer Code & Monospace)
function renderTechnicalTemplate(p) {
    const codeTag = `<${p.degreeBranch.toLowerCase().replace(/[^a-z0-9]/g, '_')} />`;
    return `
    <div class="tpl-technical">
        <header class="tech-header">
            <div>
                <h1 class="tech-name">${escapeHtml(p.fullName)}</h1>
            </div>
            <div class="tech-tag">${escapeHtml(codeTag)}</div>
        </header>
        <div class="tech-contact">
            contact: ${escapeHtml(p.email)} | tel: ${escapeHtml(p.phone)} | loc: ${escapeHtml(p.address)}
        </div>

        ${p.technicalSkills ? `
        <section class="tech-section">
            <div class="tech-heading">// CORE TECHNICAL SKILLS & STACK</div>
            <div class="tech-content">${escapeHtml(p.technicalSkills)}</div>
        </section>` : ''}

        <section class="tech-section">
            <div class="tech-heading">// SUMMARY / OBJECTIVE</div>
            <div class="tech-content">${escapeHtml(p.careerObjective)}</div>
        </section>

        ${p.projects ? `
        <section class="tech-section">
            <div class="tech-heading">// SOFTWARE PROJECTS & ARCHITECTURE</div>
            <div class="tech-content">${escapeHtml(p.projects)}</div>
        </section>` : ''}

        ${p.experience ? `
        <section class="tech-section">
            <div class="tech-heading">// WORK EXPERIENCE / INTERNSHIPS</div>
            <div class="tech-content">${escapeHtml(p.experience)}</div>
        </section>` : ''}

        <section class="tech-section">
            <div class="tech-heading">// EDUCATION & CREDENTIALS</div>
            <div style="display:flex; justify-content:space-between; font-weight:700;">
                <span>${escapeHtml(p.degreeBranch)}</span>
                <span style="color:#0284c7; font-family:var(--font-mono);">[${escapeHtml(p.graduationYear)}]</span>
            </div>
            <div style="display:flex; justify-content:space-between; color:#64748b; font-size:11px;">
                <span>${escapeHtml(p.collegeName)}</span>
                <span>CGPA: ${escapeHtml(p.cgpaPercentage)}</span>
            </div>
        </section>

        ${p.certifications ? `
        <section class="tech-section">
            <div class="tech-heading">// CERTIFICATIONS</div>
            <div class="tech-content">${escapeHtml(p.certifications)}</div>
        </section>` : ''}

        ${p.achievements ? `
        <section class="tech-section">
            <div class="tech-heading">// HONORS & COMPETITIVE PROGRAMMING</div>
            <div class="tech-content">${escapeHtml(p.achievements)}</div>
        </section>` : ''}

        ${p.languages ? `
        <section class="tech-section">
            <div class="tech-heading">// LANGUAGES</div>
            <div class="tech-content">${escapeHtml(p.languages)}</div>
        </section>` : ''}
    </div>`;
}

// Template 6: Creative (Vibrant Indigo Gradient Banner)
function renderCreativeTemplate(p) {
    return `
    <div class="tpl-creative">
        <header class="cre-banner">
            <h1 class="cre-name">${escapeHtml(p.fullName)}</h1>
            <div class="cre-degree">${escapeHtml(p.degreeBranch)}</div>
            <div class="cre-contact">
                ✉ ${escapeHtml(p.email)} &bull; ☎ ${escapeHtml(p.phone)} &bull; 📍 ${escapeHtml(p.address)}
            </div>
        </header>

        <section class="cre-section">
            <span class="cre-badge">About Me / Objective</span>
            <div class="cre-content">${escapeHtml(p.careerObjective)}</div>
        </section>

        <section class="cre-section">
            <span class="cre-badge">Education</span>
            <div style="background:#f5f3ff; border:1px solid #ddd6fe; border-radius:8px; padding:12px 16px;">
                <div style="display:flex; justify-content:space-between; font-weight:700;">
                    <span>${escapeHtml(p.degreeBranch)}</span>
                    <span style="color:#6d28d9;">Class of ${escapeHtml(p.graduationYear)}</span>
                </div>
                <div style="display:flex; justify-content:space-between; color:#64748b; font-size:11px; margin-top:2px;">
                    <span>${escapeHtml(p.collegeName)}</span>
                    <strong>Score: ${escapeHtml(p.cgpaPercentage)}</strong>
                </div>
            </div>
        </section>

        ${p.technicalSkills ? `
        <section class="cre-section">
            <span class="cre-badge">Skills & Expertise</span>
            <div class="cre-content">${escapeHtml(p.technicalSkills)}</div>
        </section>` : ''}

        ${p.projects ? `
        <section class="cre-section">
            <span class="cre-badge">Featured Projects</span>
            <div class="cre-content">${escapeHtml(p.projects)}</div>
        </section>` : ''}

        ${p.experience ? `
        <section class="cre-section">
            <span class="cre-badge">Experience</span>
            <div class="cre-content">${escapeHtml(p.experience)}</div>
        </section>` : ''}

        ${p.certifications ? `
        <section class="cre-section">
            <span class="cre-badge">Certifications</span>
            <div class="cre-content">${escapeHtml(p.certifications)}</div>
        </section>` : ''}

        ${p.achievements ? `
        <section class="cre-section">
            <span class="cre-badge">Achievements</span>
            <div class="cre-content">${escapeHtml(p.achievements)}</div>
        </section>` : ''}

        ${p.languages ? `
        <section class="cre-section">
            <span class="cre-badge">Languages</span>
            <div class="cre-content">${escapeHtml(p.languages)}</div>
        </section>` : ''}
    </div>`;
}

// ==========================================================================
// 6. Manage Profiles Table & Search
// ==========================================================================
function initManageSection() {
    const searchInput = document.getElementById('manage-search-input');
    searchInput.addEventListener('input', () => {
        renderProfilesTable(searchInput.value.trim());
    });

    document.getElementById('btn-clear-search').addEventListener('click', () => {
        searchInput.value = '';
        renderProfilesTable();
    });

    document.getElementById('btn-manage-add-sample').addEventListener('click', async () => {
        await fetchAndApplySample(true);
        await saveCurrentProfile();
    });
}

function renderProfilesTable(searchQuery = '') {
    const tbody = document.getElementById('profiles-table-body');
    const counter = document.getElementById('lbl-records-count');

    let list = state.profiles;
    if (searchQuery) {
        const q = searchQuery.toLowerCase();
        list = list.filter(p =>
            (p.fullName && p.fullName.toLowerCase().includes(q)) ||
            (p.degreeBranch && p.degreeBranch.toLowerCase().includes(q)) ||
            (p.collegeName && p.collegeName.toLowerCase().includes(q)) ||
            (p.technicalSkills && p.technicalSkills.toLowerCase().includes(q))
        );
    }

    counter.textContent = `${list.length} profile${list.length === 1 ? '' : 's'} found`;

    if (list.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="9" class="empty-state-cell">
                    <div class="empty-state">
                        <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline></svg>
                        <p>${searchQuery ? 'No profiles match your search filter.' : 'No profiles saved yet.'}</p>
                    </div>
                </td>
            </tr>`;
        return;
    }

    let rowsHtml = '';
    list.forEach((p, idx) => {
        rowsHtml += `
            <tr>
                <td>${idx + 1}</td>
                <td><strong>${escapeHtml(p.fullName)}</strong></td>
                <td>${escapeHtml(p.email)}</td>
                <td>${escapeHtml(p.degreeBranch)}</td>
                <td>${escapeHtml(p.collegeName)}</td>
                <td>${escapeHtml(p.graduationYear)}</td>
                <td><span style="font-weight:700; color:#1e3a8a;">${escapeHtml(p.cgpaPercentage)}</span></td>
                <td><small style="color:#64748b;">${p.formattedLastModified || 'Just now'}</small></td>
                <td class="text-right">
                    <button class="table-action-btn" onclick="previewProfileById('${p.id}')" title="Preview Resume">👁 View</button>
                    <button class="table-action-btn" onclick="editProfileById('${p.id}')" title="Edit Profile">✏ Edit</button>
                    <button class="table-action-btn" onclick="downloadPdfById('${p.id}')" title="Download PDF via Java PDFBox">💾 PDF</button>
                    <button class="table-action-btn btn-delete" onclick="promptDeleteProfile('${p.id}', '${escapeHtml(p.fullName)}')" title="Delete Profile">🗑</button>
                </td>
            </tr>`;
    });

    tbody.innerHTML = rowsHtml;
}

function previewProfileById(id) {
    const p = state.profiles.find(item => item.id === id);
    if (p) {
        state.activeProfile = p;
        switchTab('tab-preview');
    }
}

function editProfileById(id) {
    const p = state.profiles.find(item => item.id === id);
    if (p) {
        populateFormWithProfile(p);
        switchTab('tab-builder');
    }
}

function downloadPdfById(id) {
    const url = `/api/export-pdf?id=${encodeURIComponent(id)}&template=${encodeURIComponent(state.activeTemplate)}`;
    window.open(url, '_blank');
}

// ==========================================================================
// 7. Modals & Notifications
// ==========================================================================
function initModals() {
    const modal = document.getElementById('confirm-modal');
    document.getElementById('modal-btn-cancel').addEventListener('click', () => {
        modal.classList.remove('show');
    });

    document.getElementById('modal-btn-confirm').addEventListener('click', async () => {
        if (state.deleteTargetId) {
            await executeDeleteProfile(state.deleteTargetId);
            state.deleteTargetId = null;
        }
        modal.classList.remove('show');
    });
}

function promptDeleteProfile(id, name) {
    state.deleteTargetId = id;
    document.getElementById('modal-title').textContent = 'Confirm Delete';
    document.getElementById('modal-msg').textContent = `Are you sure you want to permanently delete profile for '${name}'?`;
    document.getElementById('confirm-modal').classList.add('show');
}

async function executeDeleteProfile(id) {
    try {
        const res = await fetch(`/api/profiles?id=${encodeURIComponent(id)}`, { method: 'DELETE' });
        if (res.ok) {
            showToast('Profile deleted successfully.', 'success');
            await loadProfiles();
        } else {
            showToast('Failed to delete profile.', 'error');
        }
    } catch (e) {
        showToast('Error deleting profile: ' + e.message, 'error');
    }
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;

    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

// Helper Utilities
function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;')
              .replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
              .replace(/"/g, '&quot;')
              .replace(/'/g, '&#039;');
}

function getInitials(name) {
    if (!name) return 'U';
    const parts = name.trim().split(/\s+/);
    if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}
