
-- =====================================================
-- FLOWTIME V1 - INITIAL DATABASE SCHEMA
-- PostgreSQL 17
-- =====================================================

-- 1. ORGANIZATIONS
CREATE TABLE organizations (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    timezone VARCHAR(100) NOT NULL DEFAULT 'UTC',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_organizations_slug UNIQUE (slug)
);

-- 2. USERSs
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX uq_users_email_lower
    ON users (LOWER(email));

-- 3. ROLES
CREATE TABLE roles (
    id UUID PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

INSERT INTO roles (id, code, name, description)
VALUES
(
    '00000000-0000-0000-0000-000000000001',
    'OWNER',
    'Owner',
    'Full organization ownership'
),
(
    '00000000-0000-0000-0000-000000000002',
    'ADMIN',
    'Administrator',
    'Organization administration'
),
(
    '00000000-0000-0000-0000-000000000003',
    'MEMBER',
    'Member',
    'Standard organization member'
);

-- 4. MEMBERSHIPS
CREATE TABLE memberships (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_memberships_org_user
        UNIQUE (organization_id, user_id),

    CONSTRAINT uq_memberships_org_id
        UNIQUE (organization_id, id),

    CONSTRAINT fk_memberships_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_memberships_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_memberships_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
);

CREATE INDEX idx_memberships_user
    ON memberships(user_id);

-- 5. TEAMS
CREATE TABLE teams (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_teams_org_id
        UNIQUE (organization_id, id),

    CONSTRAINT fk_teams_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
);

-- 6. TEAM MEMBERS
CREATE TABLE team_members (
    organization_id UUID NOT NULL,
    team_id UUID NOT NULL,
    user_id UUID NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    PRIMARY KEY (organization_id, team_id, user_id),

    CONSTRAINT fk_team_members_team
        FOREIGN KEY (organization_id, team_id)
        REFERENCES teams(organization_id, id),

    CONSTRAINT fk_team_members_membership
        FOREIGN KEY (organization_id, user_id)
        REFERENCES memberships(organization_id, user_id)
);

-- 7. PROJECTS
CREATE TABLE projects (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    team_id UUID,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PLANNED',
    start_date DATE,
    due_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_projects_org_id
        UNIQUE (organization_id, id),

    CONSTRAINT fk_projects_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_projects_team
        FOREIGN KEY (organization_id, team_id)
        REFERENCES teams(organization_id, id),

    CONSTRAINT chk_projects_status
        CHECK (status IN ('PLANNED', 'ACTIVE', 'COMPLETED', 'ARCHIVED')),

    CONSTRAINT chk_projects_dates
        CHECK (due_date IS NULL OR start_date IS NULL OR due_date >= start_date)
);

CREATE INDEX idx_projects_org_status
    ON projects(organization_id, status);

-- 8. TASKS
CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    project_id UUID NOT NULL,
    assignee_user_id UUID,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'TODO',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    estimated_minutes INTEGER,
    due_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_tasks_org_id
        UNIQUE (organization_id, id),

    CONSTRAINT fk_tasks_project
        FOREIGN KEY (organization_id, project_id)
        REFERENCES projects(organization_id, id),

    CONSTRAINT fk_tasks_assignee
        FOREIGN KEY (organization_id, assignee_user_id)
        REFERENCES memberships(organization_id, user_id),

    CONSTRAINT chk_tasks_status
        CHECK (status IN ('TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE', 'CANCELLED')),

    CONSTRAINT chk_tasks_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),

    CONSTRAINT chk_tasks_estimated_minutes
        CHECK (estimated_minutes IS NULL OR estimated_minutes >= 0)
);

CREATE INDEX idx_tasks_org_project
    ON tasks(organization_id, project_id);

CREATE INDEX idx_tasks_org_assignee
    ON tasks(organization_id, assignee_user_id);

CREATE INDEX idx_tasks_org_status
    ON tasks(organization_id, status);

-- 9. TIME ENTRIES
CREATE TABLE time_entries (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL,
    task_id UUID NOT NULL,
    user_id UUID NOT NULL,
    started_at TIMESTAMPTZ NOT NULL,
    ended_at TIMESTAMPTZ,
    description TEXT,
    is_billable BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_time_entries_task
        FOREIGN KEY (organization_id, task_id)
        REFERENCES tasks(organization_id, id),

    CONSTRAINT fk_time_entries_membership
        FOREIGN KEY (organization_id, user_id)
        REFERENCES memberships(organization_id, user_id),

    CONSTRAINT chk_time_entries_duration
        CHECK (ended_at IS NULL OR ended_at >= started_at)
);

CREATE INDEX idx_time_entries_org_task
    ON time_entries(organization_id, task_id);

CREATE INDEX idx_time_entries_org_user
    ON time_entries(organization_id, user_id);

CREATE INDEX idx_time_entries_started_at
    ON time_entries(started_at);

-- One active timer per user per organization
CREATE UNIQUE INDEX uq_time_entries_active_timer
    ON time_entries(organization_id, user_id)
    WHERE ended_at IS NULL;
