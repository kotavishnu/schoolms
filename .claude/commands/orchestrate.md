# DevPipeline Orchestrator

## Phase Sequence
1. Architecture Design → `software-architect`
2. SDLC Planning → `sdlc-planner`
3. Backend Development → `senior-backend-developer`
4. Backend QA & Fixes → `backend-qa-orchestrator`
5. Frontend Development → `senior-frontend-developer`
6. Frontend QA & Fixes → `frontend-qa-orchestrator`

## Per-Phase Checklist

### Start Phase
- [ ] Announce: Phase N - [Objective]
- [ ] Load context: Previous phase summaries + preserved artifacts
- [ ] Execute agent with handoff document

### Complete Phase
- [ ] Validate exit criteria met
- [ ] Write `docs/phases/PHASE_N_<NAME>_SUMMARY.md`:
  - Decisions made
  - Artifacts created
  - Issues + resolutions
  - Next phase inputs
- [ ] Run `/clear` (preserve: REQUIREMENTS.md, architecture, APIs, summaries)
- [ ] Generate handoff document

### On Error
- [ ] Document in phase summary
- [ ] Analyze root cause
- [ ] Retry with fix (max 1 retry/phase)
- [ ] If retry fails → escalate with failure report

## Context Preservation Rules
**Keep:** REQUIREMENTS.md, architecture decisions, API contracts, phase summaries  
**Clear:** Implementation details, resolved issues, intermediate artifacts

## Execution
Status: INITIALIZED | Phase: 0/6 | Checkpoint: None  
**Action:** Start  with `software-architect`